#!/usr/bin/env python
import httplib
import os
import hashlib
import re
import sys
from os.path import join
import os.path
import subprocess
import urllib
import json
from subprocess import Popen, PIPE

testing = raw_input('Push to testing? [y/n](n): ') == "y"

maven_central = "central.maven.org"

base_url = ""
checksum_url = "%s/live/checksums" % base_url
static_url = "%s/live/" % base_url
file_path = "../core/target/dependencies"
remote_user = ''
remote_server = ''
remote_dir = '/opt/scapelog/static'
library_dir = '%s/live' % remote_dir

cloudflare_email = ''
cloudflare_token = ''
cloudflare_zone = ''

if testing:
    checksum_url = "%s/test/checksums" % base_url
    static_url = "%s/test/" % base_url
    library_dir = '%s/test' % remote_dir

#####
#
# DIRECTORIES MUST EXIST ON THE REMOTE SERVER, OTHERWISE RSYNC WILL NOT WORK!!
#
#####


def get_loader_version():
    checksum_file = './out/checksums'
    if os.path.isfile(checksum_file):
        with open(checksum_file, 'r') as f:
            line = f.readline()
            print "Current loader version: %s" % line

    loader_version = int(raw_input('Loader version: '))
    output = open(checksum_file, 'w')
    output.write("%d\n" % loader_version)

    return loader_version, output


def push_file(name, path, local_dir):
    local_name = name
    if name == 'scapelog.jar':
        local_name = 'client.jar'
    # print "%s - %s - %s - %s" % (name, path, dir, join(dir, name))
    str = "rsync -aqzhe ssh --progress --checksum --timeout=10 %s %s@%s:%s" % (join(path, local_name), remote_user, remote_server, join(local_dir, name))
    print str
    subprocess.call(str, shell=True)


def get_dependencies():
    files = []
    cmd = "cd ../core && mvn -o dependency:list | grep \":.*:.*:.*\" | cut -d] -f2- | sed \"s/^[ |+\\-]*//\" | sort -u"
    pipe = Popen(cmd, stdout=PIPE, shell=True)
    text = pipe.communicate()[0]
    for line in iter(text.splitlines()):
        if line.startswith("Finished"):
            continue
        if line.startswith("junit"):
            continue
        parts = line.split(":")
        group = parts[0]
        artifact = parts[1]
        format = parts[2]
        version = parts[3]
        scope = parts[4]

        if not scope.startswith("compile"):
            continue

        group = group.replace(".", "/")
        url = "/maven2/%s/%s/%s/%s-%s.%s" % (group, artifact, version, artifact, version, format)
        valid = is_valid_dependency_url(url)
        url = "http://%s%s" % (maven_central, url)
        if not valid:
            print "%s not found on maven central! url: ", url
            sys.exit(1)

        name = "%s-%s.%s" % (artifact, version, format)
        path = join(file_path, name)
        files.append({
            "name": name,
            "url": url,
            "path": path
        })
    return files


def is_valid_dependency_url(url):
    conn = httplib.HTTPConnection(maven_central)
    conn.request("HEAD", url)
    res = conn.getresponse()
    return res.status == 200


def clear_cache(clear_url):
    host = "www.cloudflare.com"
    url = "/api_json.html"
    values = {
        'email': cloudflare_email,
        'tkn': cloudflare_token,
        'a': 'zone_file_purge',
        'z': cloudflare_zone,
        'url': clear_url
    }
    headers = {
        'User-Agent': 'python',
        'Content-Type': 'application/x-www-form-urlencoded',
        }
    values = urllib.urlencode(values)
    connection = httplib.HTTPSConnection(host)
    connection.request("POST", url, values, headers)
    response = connection.getresponse()

    data = json.loads(response.read())
    result = data["result"]
    if result == 'success':
        print "Successfully cleared the cache for url '%s'" % clear_url


def get_md5(fn):
    checksum = hashlib.md5()
    with open(fn, 'rb') as f:
        for chunk in iter(lambda: f.read(128 * checksum.block_size), b''):
            checksum.update(chunk)
    return checksum.hexdigest()


def write_checksums(output):
    # write dependency checksums
    files = get_dependencies()
    for file in files:
        name = file["name"]
        path = file["path"]
        url = file["url"]
        md5 = get_md5(path)
        output.write('lib$$%s$$%s$$%s\n' % (name, md5, url))
        print "%s: %s" % (name, md5)

    upload_files = {}
    # write client checksum
    path = os.path.dirname(os.path.abspath(__file__))
    client_path = join(path, 'out')
    local_client_name = 'client.jar'
    client_name = 'scapelog.jar'
    client_md5 = hashlib.md5(open(join(client_path, local_client_name)).read()).hexdigest()
    print "%s: %s" % (local_client_name, client_md5)
    output.write('main$$%s$$%s$$%s' % (client_name, client_md5, static_url + client_name))

    upload_files[client_name] = client_path

    output.close()
    return upload_files


def push_files(files):
    # push files
    for file_name in files:
        path = files[file_name]

        print "pushing %s..." % file_name
        push_file(file_name, path, library_dir)

    # push checksum file
    print "pushing checksums..."
    push_file('checksums', 'out', library_dir)


def check_debug():
    search_str = 'debug = true;'
    class_name = "../core/src/main/java/com/scapelog/client/ScapeLog.java"

    f = open(class_name, 'r')
    debug_on = False
    for line in f:
        if re.search(search_str, line):
            debug_on = True
    f.close()
    return debug_on

if check_debug():
    print "Debug is enabled!"
    sys.exit()

loader_version, output = get_loader_version()
push_files(write_checksums(output))
clear_cache(static_url + '*')
clear_cache(static_url + 'scapelog.jar')
clear_cache(checksum_url)