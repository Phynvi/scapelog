package com.scapelog.tools.diagnostics;

import com.scapelog.tools.Tool;

import java.math.BigInteger;
import java.util.Scanner;

public final class DiagnosticsReader extends Tool {

	private static final BigInteger MODULUS = new BigInteger("4213478309119431064218323995962389210721666869979509729860118007368600468970073722850459118558650604901403086106517841848560063352924705213846717796022761477055370486329226142133450375317050740349302998427110365268010999768527882802303682127878206868290725131274703151487333656900363210185285793443421");
	private static final BigInteger PRIVATE_KEY = new BigInteger("735881604684086973176113286506637577336774631029578228603367726816012343681149027751443536796348853681148965066682991558945610649519754884686353233947686589053267131298531721513540691972137610681695803435332986813246133919199769567875838106657805547193302269281082743021295527764020588160573552617025");

	public DiagnosticsReader() {
		super("diagnostics", "Diagnostics reader", "Decrypt the RSA encrypted diagnostics from users");
	}

	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter the diagnostics:\n");
		String rsaDiagnostics = scanner.nextLine();

		System.out.println("\noutput:\n------------------------------");

		String diagnostics = decrypt(rsaDiagnostics);
		System.out.println(diagnostics);
	}

	private String decrypt(String encrypted) {
		if (encrypted == null) {
			return null;
		}
		BigInteger decrypted = new BigInteger(encrypted).modPow(PRIVATE_KEY, MODULUS);
		return new String(decrypted.toByteArray());
	}

}