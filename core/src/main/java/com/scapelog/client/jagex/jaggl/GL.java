package com.scapelog.client.jagex.jaggl;


import com.scapelog.client.loader.analyser.impl.detours.Proxy;
import com.scapelog.util.proguard.KeepClassMemberNames;

@Proxy("jaggl/OpenGL")
@KeepClassMemberNames
public interface GL {
    /**
     * AccumOp
     */
    public static final int
            GL_ACCUM = 0x100,
            GL_LOAD = 0x101,
            GL_RETURN = 0x102,
            GL_MULT = 0x103,
            GL_ADD = 0x104;

    /**
     * AlphaFunction
     */
    public static final int
            GL_NEVER = 0x200,
            GL_LESS = 0x201,
            GL_EQUAL = 0x202,
            GL_LEQUAL = 0x203,
            GL_GREATER = 0x204,
            GL_NOTEQUAL = 0x205,
            GL_GEQUAL = 0x206,
            GL_ALWAYS = 0x207;

    /**
     * AttribMask
     */
    public static final int
            GL_CURRENT_BIT = 0x1,
            GL_POINT_BIT = 0x2,
            GL_LINE_BIT = 0x4,
            GL_POLYGON_BIT = 0x8,
            GL_POLYGON_STIPPLE_BIT = 0x10,
            GL_PIXEL_MODE_BIT = 0x20,
            GL_LIGHTING_BIT = 0x40,
            GL_FOG_BIT = 0x80,
            GL_DEPTH_BUFFER_BIT = 0x100,
            GL_ACCUM_BUFFER_BIT = 0x200,
            GL_STENCIL_BUFFER_BIT = 0x400,
            GL_VIEWPORT_BIT = 0x800,
            GL_TRANSFORM_BIT = 0x1000,
            GL_ENABLE_BIT = 0x2000,
            GL_COLOR_BUFFER_BIT = 0x4000,
            GL_HINT_BIT = 0x8000,
            GL_EVAL_BIT = 0x10000,
            GL_LIST_BIT = 0x20000,
            GL_TEXTURE_BIT = 0x40000,
            GL_SCISSOR_BIT = 0x80000,
            GL_ALL_ATTRIB_BITS = 0xFFFFF;

    /**
     * BeginMode
     */
    public static final int
            GL_POINTS = 0x0,
            GL_LINES = 0x1,
            GL_LINE_LOOP = 0x2,
            GL_LINE_STRIP = 0x3,
            GL_TRIANGLES = 0x4,
            GL_TRIANGLE_STRIP = 0x5,
            GL_TRIANGLE_FAN = 0x6,
            GL_QUADS = 0x7,
            GL_QUAD_STRIP = 0x8,
            GL_POLYGON = 0x9;

    /**
     * BlendingFactorDest
     */
    public static final int
            GL_ZERO = 0x0,
            GL_ONE = 0x1,
            GL_SRC_COLOR = 0x300,
            GL_ONE_MINUS_SRC_COLOR = 0x301,
            GL_SRC_ALPHA = 0x302,
            GL_ONE_MINUS_SRC_ALPHA = 0x303,
            GL_DST_ALPHA = 0x304,
            GL_ONE_MINUS_DST_ALPHA = 0x305;

    /**
     * BlendingFactorSrc
     */
    public static final int
            GL_DST_COLOR = 0x306,
            GL_ONE_MINUS_DST_COLOR = 0x307,
            GL_SRC_ALPHA_SATURATE = 0x308,
            GL_CONSTANT_COLOR = 0x8001,
            GL_ONE_MINUS_CONSTANT_COLOR = 0x8002,
            GL_CONSTANT_ALPHA = 0x8003,
            GL_ONE_MINUS_CONSTANT_ALPHA = 0x8004;

    /**
     * Boolean
     */
    public static final int
            GL_TRUE = 0x1,
            GL_FALSE = 0x0;

    /**
     * ClipPlaneName
     */
    public static final int
            GL_CLIP_PLANE0 = 0x3000,
            GL_CLIP_PLANE1 = 0x3001,
            GL_CLIP_PLANE2 = 0x3002,
            GL_CLIP_PLANE3 = 0x3003,
            GL_CLIP_PLANE4 = 0x3004,
            GL_CLIP_PLANE5 = 0x3005;

    /**
     * DataType
     */
    public static final int
            GL_BYTE = 0x1400,
            GL_UNSIGNED_BYTE = 0x1401,
            GL_SHORT = 0x1402,
            GL_UNSIGNED_SHORT = 0x1403,
            GL_INT = 0x1404,
            GL_UNSIGNED_INT = 0x1405,
            GL_FLOAT = 0x1406,
            GL_2_BYTES = 0x1407,
            GL_3_BYTES = 0x1408,
            GL_4_BYTES = 0x1409,
            GL_DOUBLE = 0x140A;

    /**
     * DrawBufferMode
     */
    public static final int
            GL_NONE = 0x0,
            GL_FRONT_LEFT = 0x400,
            GL_FRONT_RIGHT = 0x401,
            GL_BACK_LEFT = 0x402,
            GL_BACK_RIGHT = 0x403,
            GL_FRONT = 0x404,
            GL_BACK = 0x405,
            GL_LEFT = 0x406,
            GL_RIGHT = 0x407,
            GL_FRONT_AND_BACK = 0x408,
            GL_AUX0 = 0x409,
            GL_AUX1 = 0x40A,
            GL_AUX2 = 0x40B,
            GL_AUX3 = 0x40C;

    /**
     * ErrorCode
     */
    public static final int
            GL_NO_ERROR = 0x0,
            GL_INVALID_ENUM = 0x500,
            GL_INVALID_VALUE = 0x501,
            GL_INVALID_OPERATION = 0x502,
            GL_STACK_OVERFLOW = 0x503,
            GL_STACK_UNDERFLOW = 0x504,
            GL_OUT_OF_MEMORY = 0x505;

    /**
     * FeedBackMode
     */
    public static final int
            GL_2D = 0x600,
            GL_3D = 0x601,
            GL_3D_COLOR = 0x602,
            GL_3D_COLOR_TEXTURE = 0x603,
            GL_4D_COLOR_TEXTURE = 0x604;

    /**
     * FeedBackToken
     */
    public static final int
            GL_PASS_THROUGH_TOKEN = 0x700,
            GL_POINT_TOKEN = 0x701,
            GL_LINE_TOKEN = 0x702,
            GL_POLYGON_TOKEN = 0x703,
            GL_BITMAP_TOKEN = 0x704,
            GL_DRAW_PIXEL_TOKEN = 0x705,
            GL_COPY_PIXEL_TOKEN = 0x706,
            GL_LINE_RESET_TOKEN = 0x707;

    /**
     * FogMode
     */
    public static final int
            GL_EXP = 0x800,
            GL_EXP2 = 0x801;

    /**
     * FrontFaceDirection
     */
    public static final int
            GL_CW = 0x900,
            GL_CCW = 0x901;

    /**
     * GetMapTarget
     */
    public static final int
            GL_COEFF = 0xA00,
            GL_ORDER = 0xA01,
            GL_DOMAIN = 0xA02;

    /**
     * GetTarget
     */
    public static final int
            GL_CURRENT_COLOR = 0xB00,
            GL_CURRENT_NORMAL = 0xB02,
            GL_CURRENT_TEXTURE_COORDS = 0xB03,
            GL_CURRENT_RASTER_COLOR = 0xB04,
            GL_CURRENT_RASTER_TEXTURE_COORDS = 0xB06,
            GL_CURRENT_RASTER_POSITION = 0xB07,
            GL_CURRENT_RASTER_POSITION_VALID = 0xB08,
            GL_CURRENT_RASTER_DISTANCE = 0xB09,
            GL_POINT_SMOOTH = 0xB10,
            GL_POINT_SIZE = 0xB11,
            GL_POINT_SIZE_RANGE = 0xB12,
            GL_POINT_SIZE_GRANULARITY = 0xB13,
            GL_LINE_SMOOTH = 0xB20,
            GL_LINE_WIDTH = 0xB21,
            GL_LINE_WIDTH_RANGE = 0xB22,
            GL_LINE_WIDTH_GRANULARITY = 0xB23,
            GL_LINE_STIPPLE = 0xB24,
            GL_LINE_STIPPLE_PATTERN = 0xB25,
            GL_LINE_STIPPLE_REPEAT = 0xB26,
            GL_LIST_MODE = 0xB30,
            GL_MAX_LIST_NESTING = 0xB31,
            GL_LIST_BASE = 0xB32,
            GL_LIST_INDEX = 0xB33,
            GL_POLYGON_MODE = 0xB40,
            GL_POLYGON_SMOOTH = 0xB41,
            GL_POLYGON_STIPPLE = 0xB42,
            GL_EDGE_FLAG = 0xB43,
            GL_CULL_FACE = 0xB44,
            GL_CULL_FACE_MODE = 0xB45,
            GL_FRONT_FACE = 0xB46,
            GL_LIGHTING = 0xB50,
            GL_LIGHT_MODEL_LOCAL_VIEWER = 0xB51,
            GL_LIGHT_MODEL_TWO_SIDE = 0xB52,
            GL_LIGHT_MODEL_AMBIENT = 0xB53,
            GL_SHADE_MODEL = 0xB54,
            GL_COLOR_MATERIAL_FACE = 0xB55,
            GL_COLOR_MATERIAL_PARAMETER = 0xB56,
            GL_COLOR_MATERIAL = 0xB57,
            GL_FOG = 0xB60,
            GL_FOG_DENSITY = 0xB62,
            GL_FOG_START = 0xB63,
            GL_FOG_END = 0xB64,
            GL_FOG_MODE = 0xB65,
            GL_FOG_COLOR = 0xB66,
            GL_DEPTH_RANGE = 0xB70,
            GL_DEPTH_TEST = 0xB71,
            GL_DEPTH_WRITEMASK = 0xB72,
            GL_DEPTH_CLEAR_VALUE = 0xB73,
            GL_DEPTH_FUNC = 0xB74,
            GL_ACCUM_CLEAR_VALUE = 0xB80,
            GL_STENCIL_TEST = 0xB90,
            GL_STENCIL_CLEAR_VALUE = 0xB91,
            GL_STENCIL_FUNC = 0xB92,
            GL_STENCIL_VALUE_MASK = 0xB93,
            GL_STENCIL_FAIL = 0xB94,
            GL_STENCIL_PASS_DEPTH_FAIL = 0xB95,
            GL_STENCIL_PASS_DEPTH_PASS = 0xB96,
            GL_STENCIL_REF = 0xB97,
            GL_STENCIL_WRITEMASK = 0xB98,
            GL_MATRIX_MODE = 0xBA0,
            GL_NORMALIZE = 0xBA1,
            GL_VIEWPORT = 0xBA2,
            GL_MODELVIEW_STACK_DEPTH = 0xBA3,
            GL_PROJECTION_STACK_DEPTH = 0xBA4,
            GL_TEXTURE_STACK_DEPTH = 0xBA5,
            GL_MODELVIEW_MATRIX = 0xBA6,
            GL_PROJECTION_MATRIX = 0xBA7,
            GL_TEXTURE_MATRIX = 0xBA8,
            GL_ATTRIB_STACK_DEPTH = 0xBB0,
            GL_CLIENT_ATTRIB_STACK_DEPTH = 0xBB1,
            GL_ALPHA_TEST = 0xBC0,
            GL_ALPHA_TEST_FUNC = 0xBC1,
            GL_ALPHA_TEST_REF = 0xBC2,
            GL_DITHER = 0xBD0,
            GL_BLEND_DST = 0xBE0,
            GL_BLEND_SRC = 0xBE1,
            GL_BLEND = 0xBE2,
            GL_LOGIC_OP_MODE = 0xBF0,
            GL_COLOR_LOGIC_OP = 0xBF2,
            GL_AUX_BUFFERS = 0xC00,
            GL_DRAW_BUFFER = 0xC01,
            GL_READ_BUFFER = 0xC02,
            GL_SCISSOR_BOX = 0xC10,
            GL_SCISSOR_TEST = 0xC11,
            GL_COLOR_CLEAR_VALUE = 0xC22,
            GL_COLOR_WRITEMASK = 0xC23,
            GL_RGBA_MODE = 0xC31,
            GL_DOUBLEBUFFER = 0xC32,
            GL_STEREO = 0xC33,
            GL_RENDER_MODE = 0xC40,
            GL_PERSPECTIVE_CORRECTION_HINT = 0xC50,
            GL_POINT_SMOOTH_HINT = 0xC51,
            GL_LINE_SMOOTH_HINT = 0xC52,
            GL_POLYGON_SMOOTH_HINT = 0xC53,
            GL_FOG_HINT = 0xC54,
            GL_TEXTURE_GEN_S = 0xC60,
            GL_TEXTURE_GEN_T = 0xC61,
            GL_TEXTURE_GEN_R = 0xC62,
            GL_TEXTURE_GEN_Q = 0xC63,
            GL_PIXEL_MAP_I_TO_I = 0xC70,
            GL_PIXEL_MAP_S_TO_S = 0xC71,
            GL_PIXEL_MAP_I_TO_R = 0xC72,
            GL_PIXEL_MAP_I_TO_G = 0xC73,
            GL_PIXEL_MAP_I_TO_B = 0xC74,
            GL_PIXEL_MAP_I_TO_A = 0xC75,
            GL_PIXEL_MAP_R_TO_R = 0xC76,
            GL_PIXEL_MAP_G_TO_G = 0xC77,
            GL_PIXEL_MAP_B_TO_B = 0xC78,
            GL_PIXEL_MAP_A_TO_A = 0xC79,
            GL_PIXEL_MAP_I_TO_I_SIZE = 0xCB0,
            GL_PIXEL_MAP_S_TO_S_SIZE = 0xCB1,
            GL_PIXEL_MAP_I_TO_R_SIZE = 0xCB2,
            GL_PIXEL_MAP_I_TO_G_SIZE = 0xCB3,
            GL_PIXEL_MAP_I_TO_B_SIZE = 0xCB4,
            GL_PIXEL_MAP_I_TO_A_SIZE = 0xCB5,
            GL_PIXEL_MAP_R_TO_R_SIZE = 0xCB6,
            GL_PIXEL_MAP_G_TO_G_SIZE = 0xCB7,
            GL_PIXEL_MAP_B_TO_B_SIZE = 0xCB8,
            GL_PIXEL_MAP_A_TO_A_SIZE = 0xCB9,
            GL_UNPACK_SWAP_BYTES = 0xCF0,
            GL_UNPACK_LSB_FIRST = 0xCF1,
            GL_UNPACK_ROW_LENGTH = 0xCF2,
            GL_UNPACK_SKIP_ROWS = 0xCF3,
            GL_UNPACK_SKIP_PIXELS = 0xCF4,
            GL_UNPACK_ALIGNMENT = 0xCF5,
            GL_PACK_SWAP_BYTES = 0xD00,
            GL_PACK_LSB_FIRST = 0xD01,
            GL_PACK_ROW_LENGTH = 0xD02,
            GL_PACK_SKIP_ROWS = 0xD03,
            GL_PACK_SKIP_PIXELS = 0xD04,
            GL_PACK_ALIGNMENT = 0xD05,
            GL_MAP_COLOR = 0xD10,
            GL_MAP_STENCIL = 0xD11,
            GL_INDEX_SHIFT = 0xD12,
            GL_INDEX_OFFSET = 0xD13,
            GL_RED_SCALE = 0xD14,
            GL_RED_BIAS = 0xD15,
            GL_ZOOM_X = 0xD16,
            GL_ZOOM_Y = 0xD17,
            GL_GREEN_SCALE = 0xD18,
            GL_GREEN_BIAS = 0xD19,
            GL_BLUE_SCALE = 0xD1A,
            GL_BLUE_BIAS = 0xD1B,
            GL_ALPHA_SCALE = 0xD1C,
            GL_ALPHA_BIAS = 0xD1D,
            GL_DEPTH_SCALE = 0xD1E,
            GL_DEPTH_BIAS = 0xD1F,
            GL_MAX_EVAL_ORDER = 0xD30,
            GL_MAX_LIGHTS = 0xD31,
            GL_MAX_CLIP_PLANES = 0xD32,
            GL_MAX_TEXTURE_SIZE = 0xD33,
            GL_MAX_PIXEL_MAP_TABLE = 0xD34,
            GL_MAX_ATTRIB_STACK_DEPTH = 0xD35,
            GL_MAX_MODELVIEW_STACK_DEPTH = 0xD36,
            GL_MAX_NAME_STACK_DEPTH = 0xD37,
            GL_MAX_PROJECTION_STACK_DEPTH = 0xD38,
            GL_MAX_TEXTURE_STACK_DEPTH = 0xD39,
            GL_MAX_VIEWPORT_DIMS = 0xD3A,
            GL_MAX_CLIENT_ATTRIB_STACK_DEPTH = 0xD3B,
            GL_SUBPIXEL_BITS = 0xD50,
            GL_RED_BITS = 0xD52,
            GL_GREEN_BITS = 0xD53,
            GL_BLUE_BITS = 0xD54,
            GL_ALPHA_BITS = 0xD55,
            GL_DEPTH_BITS = 0xD56,
            GL_STENCIL_BITS = 0xD57,
            GL_ACCUM_RED_BITS = 0xD58,
            GL_ACCUM_GREEN_BITS = 0xD59,
            GL_ACCUM_BLUE_BITS = 0xD5A,
            GL_ACCUM_ALPHA_BITS = 0xD5B,
            GL_NAME_STACK_DEPTH = 0xD70,
            GL_AUTO_NORMAL = 0xD80,
            GL_MAP1_COLOR_4 = 0xD90,
            GL_MAP1_NORMAL = 0xD92,
            GL_MAP1_TEXTURE_COORD_1 = 0xD93,
            GL_MAP1_TEXTURE_COORD_2 = 0xD94,
            GL_MAP1_TEXTURE_COORD_3 = 0xD95,
            GL_MAP1_TEXTURE_COORD_4 = 0xD96,
            GL_MAP1_VERTEX_3 = 0xD97,
            GL_MAP1_VERTEX_4 = 0xD98,
            GL_MAP2_COLOR_4 = 0xDB0,
            GL_MAP2_NORMAL = 0xDB2,
            GL_MAP2_TEXTURE_COORD_1 = 0xDB3,
            GL_MAP2_TEXTURE_COORD_2 = 0xDB4,
            GL_MAP2_TEXTURE_COORD_3 = 0xDB5,
            GL_MAP2_TEXTURE_COORD_4 = 0xDB6,
            GL_MAP2_VERTEX_3 = 0xDB7,
            GL_MAP2_VERTEX_4 = 0xDB8,
            GL_MAP1_GRID_DOMAIN = 0xDD0,
            GL_MAP1_GRID_SEGMENTS = 0xDD1,
            GL_MAP2_GRID_DOMAIN = 0xDD2,
            GL_MAP2_GRID_SEGMENTS = 0xDD3,
            GL_TEXTURE_1D = 0xDE0,
            GL_TEXTURE_2D = 0xDE1,
            GL_FEEDBACK_BUFFER_POINTER = 0xDF0,
            GL_FEEDBACK_BUFFER_SIZE = 0xDF1,
            GL_FEEDBACK_BUFFER_TYPE = 0xDF2,
            GL_SELECTION_BUFFER_POINTER = 0xDF3,
            GL_SELECTION_BUFFER_SIZE = 0xDF4;

    /**
     * GetTextureParameter
     */
    public static final int
            GL_TEXTURE_WIDTH = 0x1000,
            GL_TEXTURE_HEIGHT = 0x1001,
            GL_TEXTURE_INTERNAL_FORMAT = 0x1003,
            GL_TEXTURE_BORDER_COLOR = 0x1004,
            GL_TEXTURE_BORDER = 0x1005;

    /**
     * HintMode
     */
    public static final int
            GL_DONT_CARE = 0x1100,
            GL_FASTEST = 0x1101,
            GL_NICEST = 0x1102;

    /**
     * LightName
     */
    public static final int
            GL_LIGHT0 = 0x4000,
            GL_LIGHT1 = 0x4001,
            GL_LIGHT2 = 0x4002,
            GL_LIGHT3 = 0x4003,
            GL_LIGHT4 = 0x4004,
            GL_LIGHT5 = 0x4005,
            GL_LIGHT6 = 0x4006,
            GL_LIGHT7 = 0x4007;

    /**
     * LightParameter
     */
    public static final int
            GL_AMBIENT = 0x1200,
            GL_DIFFUSE = 0x1201,
            GL_SPECULAR = 0x1202,
            GL_POSITION = 0x1203,
            GL_SPOT_DIRECTION = 0x1204,
            GL_SPOT_EXPONENT = 0x1205,
            GL_SPOT_CUTOFF = 0x1206,
            GL_CONSTANT_ATTENUATION = 0x1207,
            GL_LINEAR_ATTENUATION = 0x1208,
            GL_QUADRATIC_ATTENUATION = 0x1209;

    /**
     * ListMode
     */
    public static final int
            GL_COMPILE = 0x1300,
            GL_COMPILE_AND_EXECUTE = 0x1301;

    /**
     * LogicOp
     */
    public static final int
            GL_CLEAR = 0x1500,
            GL_AND = 0x1501,
            GL_AND_REVERSE = 0x1502,
            GL_COPY = 0x1503,
            GL_AND_INVERTED = 0x1504,
            GL_NOOP = 0x1505,
            GL_XOR = 0x1506,
            GL_OR = 0x1507,
            GL_NOR = 0x1508,
            GL_EQUIV = 0x1509,
            GL_INVERT = 0x150A,
            GL_OR_REVERSE = 0x150B,
            GL_COPY_INVERTED = 0x150C,
            GL_OR_INVERTED = 0x150D,
            GL_NAND = 0x150E,
            GL_SET = 0x150F;

    /**
     * MaterialParameter
     */
    public static final int
            GL_EMISSION = 0x1600,
            GL_SHININESS = 0x1601,
            GL_AMBIENT_AND_DIFFUSE = 0x1602;

    /**
     * MatrixMode
     */
    public static final int
            GL_MODELVIEW = 5888,
            GL_PROJECTION = 5889,
            GL_TEXTURE = 5890;

    /**
     * PixelCopyType
     */
    public static final int
            GL_COLOR = 0x1800,
            GL_DEPTH = 0x1801,
            GL_STENCIL = 0x1802;

    /**
     * PixelFormat
     */
    public static final int
            GL_STENCIL_INDEX = 0x1901,
            GL_DEPTH_COMPONENT = 0x1902,
            GL_RED = 0x1903,
            GL_GREEN = 0x1904,
            GL_BLUE = 0x1905,
            GL_ALPHA = 0x1906,
            GL_RGB = 0x1907,
            GL_RGBA = 0x1908,
            GL_LUMINANCE = 0x1909,
            GL_LUMINANCE_ALPHA = 0x190A;

    /**
     * PixelType
     */
    public static final int
            GL_BITMAP = 0x1A00;

    /**
     * PolygonMode
     */
    public static final int
            GL_POINT = 0x1B00,
            GL_LINE = 0x1B01,
            GL_FILL = 0x1B02;

    /**
     * RenderingMode
     */
    public static final int
            GL_RENDER = 0x1C00,
            GL_FEEDBACK = 0x1C01,
            GL_SELECT = 0x1C02;

    /**
     * ShadingModel
     */
    public static final int
            GL_FLAT = 0x1D00,
            GL_SMOOTH = 0x1D01;

    /**
     * StencilOp
     */
    public static final int
            GL_KEEP = 0x1E00,
            GL_REPLACE = 0x1E01,
            GL_INCR = 0x1E02,
            GL_DECR = 0x1E03;

    /**
     * StringName
     */
    public static final int
            GL_VENDOR = 0x1F00,
            GL_RENDERER = 0x1F01,
            GL_VERSION = 0x1F02,
            GL_EXTENSIONS = 0x1F03;

    /**
     * TextureCoordName
     */
    public static final int
            GL_S = 0x2000,
            GL_T = 0x2001,
            GL_R = 0x2002,
            GL_Q = 0x2003;

    /**
     * TextureEnvMode
     */
    public static final int
            GL_MODULATE = 0x2100,
            GL_DECAL = 0x2101;

    /**
     * TextureEnvParameter
     */
    public static final int
            GL_TEXTURE_ENV_MODE = 0x2200,
            GL_TEXTURE_ENV_COLOR = 0x2201;

    /**
     * TextureEnvTarget
     */
    public static final int
            GL_TEXTURE_ENV = 0x2300;

    /**
     * TextureGenMode
     */
    public static final int
            GL_EYE_LINEAR = 0x2400,
            GL_OBJECT_LINEAR = 0x2401,
            GL_SPHERE_MAP = 0x2402;

    /**
     * TextureGenParameter
     */
    public static final int
            GL_TEXTURE_GEN_MODE = 0x2500,
            GL_OBJECT_PLANE = 0x2501,
            GL_EYE_PLANE = 0x2502;

    /**
     * TextureMagFilter
     */
    public static final int
            GL_NEAREST = 0x2600,
            GL_LINEAR = 0x2601;

    /**
     * TextureMinFilter
     */
    public static final int
            GL_NEAREST_MIPMAP_NEAREST = 0x2700,
            GL_LINEAR_MIPMAP_NEAREST = 0x2701,
            GL_NEAREST_MIPMAP_LINEAR = 0x2702,
            GL_LINEAR_MIPMAP_LINEAR = 0x2703;

    /**
     * TextureParameterName
     */
    public static final int
            GL_TEXTURE_MAG_FILTER = 0x2800,
            GL_TEXTURE_MIN_FILTER = 0x2801,
            GL_TEXTURE_WRAP_S = 0x2802,
            GL_TEXTURE_WRAP_T = 0x2803;

    /**
     * TextureWrapMode
     */
    public static final int
            GL_CLAMP = 0x2900,
            GL_REPEAT = 0x2901;

    /**
     * ClientAttribMask
     */
    public static final int
            GL_CLIENT_PIXEL_STORE_BIT = 0x1,
            GL_CLIENT_VERTEX_ARRAY_BIT = 0x2,
            GL_CLIENT_ALL_ATTRIB_BITS = 0xFFFFFFFF;

    /**
     * polygon_offset
     */
    public static final int
            GL_POLYGON_OFFSET_FACTOR = 0x8038,
            GL_POLYGON_OFFSET_UNITS = 0x2A00,
            GL_POLYGON_OFFSET_POINT = 0x2A01,
            GL_POLYGON_OFFSET_LINE = 0x2A02,
            GL_POLYGON_OFFSET_FILL = 0x8037;

    /**
     * textureId
     */
    public static final int
            GL_ALPHA4 = 0x803B,
            GL_ALPHA8 = 0x803C,
            GL_ALPHA12 = 0x803D,
            GL_ALPHA16 = 0x803E,
            GL_LUMINANCE4 = 0x803F,
            GL_LUMINANCE8 = 0x8040,
            GL_LUMINANCE12 = 0x8041,
            GL_LUMINANCE16 = 0x8042,
            GL_LUMINANCE4_ALPHA4 = 0x8043,
            GL_LUMINANCE6_ALPHA2 = 0x8044,
            GL_LUMINANCE8_ALPHA8 = 0x8045,
            GL_LUMINANCE12_ALPHA4 = 0x8046,
            GL_LUMINANCE12_ALPHA12 = 0x8047,
            GL_LUMINANCE16_ALPHA16 = 0x8048,
            GL_INTENSITY = 0x8049,
            GL_INTENSITY4 = 0x804A,
            GL_INTENSITY8 = 0x804B,
            GL_INTENSITY12 = 0x804C,
            GL_INTENSITY16 = 0x804D,
            GL_R3_G3_B2 = 0x2A10,
            GL_RGB4 = 0x804F,
            GL_RGB5 = 0x8050,
            GL_RGB8 = 0x8051,
            GL_RGB10 = 0x8052,
            GL_RGB12 = 0x8053,
            GL_RGB16 = 0x8054,
            GL_RGBA2 = 0x8055,
            GL_RGBA4 = 0x8056,
            GL_RGB5_A1 = 0x8057,
            GL_RGBA8 = 0x8058,
            GL_RGB10_A2 = 0x8059,
            GL_RGBA12 = 0x805A,
            GL_RGBA16 = 0x805B,
            GL_TEXTURE_RED_SIZE = 0x805C,
            GL_TEXTURE_GREEN_SIZE = 0x805D,
            GL_TEXTURE_BLUE_SIZE = 0x805E,
            GL_TEXTURE_ALPHA_SIZE = 0x805F,
            GL_TEXTURE_LUMINANCE_SIZE = 0x8060,
            GL_TEXTURE_INTENSITY_SIZE = 0x8061,
            GL_PROXY_TEXTURE_1D = 0x8063,
            GL_PROXY_TEXTURE_2D = 0x8064;

    /**
     * texture_object
     */
    public static final int
            GL_TEXTURE_PRIORITY = 0x8066,
            GL_TEXTURE_RESIDENT = 0x8067,
            GL_TEXTURE_BINDING_1D = 0x8068,
            GL_TEXTURE_BINDING_2D = 0x8069;

    /**
     * vertex_array
     */
    public static final int
            GL_VERTEX_ARRAY = 0x8074,
            GL_NORMAL_ARRAY = 0x8075,
            GL_COLOR_ARRAY = 0x8076,
            GL_TEXTURE_COORD_ARRAY = 0x8078,
            GL_EDGE_FLAG_ARRAY = 0x8079,
            GL_VERTEX_ARRAY_SIZE = 0x807A,
            GL_VERTEX_ARRAY_TYPE = 0x807B,
            GL_VERTEX_ARRAY_STRIDE = 0x807C,
            GL_NORMAL_ARRAY_TYPE = 0x807E,
            GL_NORMAL_ARRAY_STRIDE = 0x807F,
            GL_COLOR_ARRAY_SIZE = 0x8081,
            GL_COLOR_ARRAY_TYPE = 0x8082,
            GL_COLOR_ARRAY_STRIDE = 0x8083,
            GL_TEXTURE_COORD_ARRAY_SIZE = 0x8088,
            GL_TEXTURE_COORD_ARRAY_TYPE = 0x8089,
            GL_TEXTURE_COORD_ARRAY_STRIDE = 0x808A,
            GL_EDGE_FLAG_ARRAY_STRIDE = 0x808C,
            GL_VERTEX_ARRAY_POINTER = 0x808E,
            GL_NORMAL_ARRAY_POINTER = 0x808F,
            GL_COLOR_ARRAY_POINTER = 0x8090,
            GL_TEXTURE_COORD_ARRAY_POINTER = 0x8092,
            GL_EDGE_FLAG_ARRAY_POINTER = 0x8093,
            GL_V2F = 0x2A20,
            GL_V3F = 0x2A21,
            GL_C4UB_V2F = 0x2A22,
            GL_C4UB_V3F = 0x2A23,
            GL_C3F_V3F = 0x2A24,
            GL_N3F_V3F = 0x2A25,
            GL_C4F_N3F_V3F = 0x2A26,
            GL_T2F_V3F = 0x2A27,
            GL_T4F_V4F = 0x2A28,
            GL_T2F_C4UB_V3F = 0x2A29,
            GL_T2F_C3F_V3F = 0x2A2A,
            GL_T2F_N3F_V3F = 0x2A2B,
            GL_T2F_C4F_N3F_V3F = 0x2A2C,
            GL_T4F_C4F_N3F_V4F = 0x2A2D;

    public static final int GL_TEXTURE_BINDING_3D = 0x806A,
            GL_PACK_SKIP_IMAGES = 0x806B,
            GL_PACK_IMAGE_HEIGHT = 0x806C,
            GL_UNPACK_SKIP_IMAGES = 0x806D,
            GL_UNPACK_IMAGE_HEIGHT = 0x806E,
            GL_TEXTURE_3D = 0x806F,
            GL_PROXY_TEXTURE_3D = 0x8070,
            GL_TEXTURE_DEPTH = 0x8071,
            GL_TEXTURE_WRAP_R = 0x8072,
            GL_MAX_3D_TEXTURE_SIZE = 0x8073,
            GL_BGR = 0x80E0,
            GL_BGRA = 0x80E1,
            GL_UNSIGNED_BYTE_3_3_2 = 0x8032,
            GL_UNSIGNED_BYTE_2_3_3_REV = 0x8362,
            GL_UNSIGNED_SHORT_5_6_5 = 0x8363,
            GL_UNSIGNED_SHORT_5_6_5_REV = 0x8364,
            GL_UNSIGNED_SHORT_4_4_4_4 = 0x8033,
            GL_UNSIGNED_SHORT_4_4_4_4_REV = 0x8365,
            GL_UNSIGNED_SHORT_5_5_5_1 = 0x8034,
            GL_UNSIGNED_SHORT_1_5_5_5_REV = 0x8366,
            GL_UNSIGNED_INT_8_8_8_8 = 0x8035,
            GL_UNSIGNED_INT_8_8_8_8_REV = 0x8367,
            GL_UNSIGNED_INT_10_10_10_2 = 0x8036,
            GL_UNSIGNED_INT_2_10_10_10_REV = 0x8368,
            GL_RESCALE_NORMAL = 0x803A,
            GL_LIGHT_MODEL_COLOR_CONTROL = 0x81F8,
            GL_SINGLE_COLOR = 0x81F9,
            GL_SEPARATE_SPECULAR_COLOR = 0x81FA,
            GL_CLAMP_TO_EDGE = 0x812F,
            GL_TEXTURE_MIN_LOD = 0x813A,
            GL_TEXTURE_MAX_LOD = 0x813B,
            GL_TEXTURE_BASE_LEVEL = 0x813C,
            GL_TEXTURE_MAX_LEVEL = 0x813D,
            GL_MAX_ELEMENTS_VERTICES = 0x80E8,
            GL_MAX_ELEMENTS_INDICES = 0x80E9,
            GL_ALIASED_POINT_SIZE_RANGE = 0x846D,
            GL_ALIASED_LINE_WIDTH_RANGE = 0x846E,
            GL_SMOOTH_POINT_SIZE_RANGE = 0xB12,
            GL_SMOOTH_POINT_SIZE_GRANULARITY = 0xB13,
            GL_SMOOTH_LINE_WIDTH_RANGE = 0xB22,
            GL_SMOOTH_LINE_WIDTH_GRANULARITY = 0xB23;
    public static final int GL_TEXTURE0 = 0x84C0,
            GL_TEXTURE1 = 0x84C1,
            GL_TEXTURE2 = 0x84C2,
            GL_TEXTURE3 = 0x84C3,
            GL_TEXTURE4 = 0x84C4,
            GL_TEXTURE5 = 0x84C5,
            GL_TEXTURE6 = 0x84C6,
            GL_TEXTURE7 = 0x84C7,
            GL_TEXTURE8 = 0x84C8,
            GL_TEXTURE9 = 0x84C9,
            GL_TEXTURE10 = 0x84CA,
            GL_TEXTURE11 = 0x84CB,
            GL_TEXTURE12 = 0x84CC,
            GL_TEXTURE13 = 0x84CD,
            GL_TEXTURE14 = 0x84CE,
            GL_TEXTURE15 = 0x84CF,
            GL_TEXTURE16 = 0x84D0,
            GL_TEXTURE17 = 0x84D1,
            GL_TEXTURE18 = 0x84D2,
            GL_TEXTURE19 = 0x84D3,
            GL_TEXTURE20 = 0x84D4,
            GL_TEXTURE21 = 0x84D5,
            GL_TEXTURE22 = 0x84D6,
            GL_TEXTURE23 = 0x84D7,
            GL_TEXTURE24 = 0x84D8,
            GL_TEXTURE25 = 0x84D9,
            GL_TEXTURE26 = 0x84DA,
            GL_TEXTURE27 = 0x84DB,
            GL_TEXTURE28 = 0x84DC,
            GL_TEXTURE29 = 0x84DD,
            GL_TEXTURE30 = 0x84DE,
            GL_TEXTURE31 = 0x84DF,
            GL_ACTIVE_TEXTURE = 0x84E0,
            GL_CLIENT_ACTIVE_TEXTURE = 0x84E1,
            GL_MAX_TEXTURE_UNITS = 0x84E2,
            GL_NORMAL_MAP = 0x8511,
            GL_REFLECTION_MAP = 0x8512,
            GL_TEXTURE_CUBE_MAP = 0x8513,
            GL_TEXTURE_BINDING_CUBE_MAP = 0x8514,
            GL_TEXTURE_CUBE_MAP_POSITIVE_X = 0x8515,
            GL_TEXTURE_CUBE_MAP_NEGATIVE_X = 0x8516,
            GL_TEXTURE_CUBE_MAP_POSITIVE_Y = 0x8517,
            GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = 0x8518,
            GL_TEXTURE_CUBE_MAP_POSITIVE_Z = 0x8519,
            GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = 0x851A,
            GL_PROXY_TEXTURE_CUBE_MAP = 0x851B,
            GL_MAX_CUBE_MAP_TEXTURE_SIZE = 0x851C,
            GL_COMPRESSED_ALPHA = 0x84E9,
            GL_COMPRESSED_LUMINANCE = 0x84EA,
            GL_COMPRESSED_LUMINANCE_ALPHA = 0x84EB,
            GL_COMPRESSED_INTENSITY = 0x84EC,
            GL_COMPRESSED_RGB = 0x84ED,
            GL_COMPRESSED_RGBA = 0x84EE,
            GL_TEXTURE_COMPRESSION_HINT = 0x84EF,
            GL_TEXTURE_COMPRESSED_IMAGE_SIZE = 0x86A0,
            GL_TEXTURE_COMPRESSED = 0x86A1,
            GL_NUM_COMPRESSED_TEXTURE_FORMATS = 0x86A2,
            GL_COMPRESSED_TEXTURE_FORMATS = 0x86A3,
            GL_MULTISAMPLE = 0x809D,
            GL_SAMPLE_ALPHA_TO_COVERAGE = 0x809E,
            GL_SAMPLE_ALPHA_TO_ONE = 0x809F,
            GL_SAMPLE_COVERAGE = 0x80A0,
            GL_SAMPLE_BUFFERS = 0x80A8,
            GL_SAMPLES = 0x80A9,
            GL_SAMPLE_COVERAGE_VALUE = 0x80AA,
            GL_SAMPLE_COVERAGE_INVERT = 0x80AB,
            GL_MULTISAMPLE_BIT = 0x20000000,
            GL_TRANSPOSE_MODELVIEW_MATRIX = 0x84E3,
            GL_TRANSPOSE_PROJECTION_MATRIX = 0x84E4,
            GL_TRANSPOSE_TEXTURE_MATRIX = 0x84E5,
            GL_TRANSPOSE_COLOR_MATRIX = 0x84E6,
            GL_COMBINE = 0x8570,
            GL_COMBINE_RGB = 0x8571,
            GL_COMBINE_ALPHA = 0x8572,
            GL_SOURCE0_RGB = 0x8580,
            GL_SOURCE1_RGB = 0x8581,
            GL_SOURCE2_RGB = 0x8582,
            GL_SOURCE0_ALPHA = 0x8588,
            GL_SOURCE1_ALPHA = 0x8589,
            GL_SOURCE2_ALPHA = 0x858A,
            GL_OPERAND0_RGB = 0x8590,
            GL_OPERAND1_RGB = 0x8591,
            GL_OPERAND2_RGB = 0x8592,
            GL_OPERAND0_ALPHA = 0x8598,
            GL_OPERAND1_ALPHA = 0x8599,
            GL_OPERAND2_ALPHA = 0x859A,
            GL_RGB_SCALE = 0x8573,
            GL_ADD_SIGNED = 0x8574,
            GL_INTERPOLATE = 0x8575,
            GL_SUBTRACT = 0x84E7,
            GL_CONSTANT = 0x8576,
            GL_PRIMARY_COLOR = 0x8577,
            GL_PREVIOUS = 0x8578,
            GL_DOT3_RGB = 0x86AE,
            GL_DOT3_RGBA = 0x86AF,
            GL_CLAMP_TO_BORDER = 0x812D;


    public static final int GL_TEXTURE_RECTANGLE_ARB = 0x84F5;

    void glShadeModel(int paramInt);

    void glHint(int paramInt1, int paramInt2);

    void glColorMask(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4);

    void glLineWidth(float paramFloat);

    void glPointSize(float paramFloat);

    void glPolygonMode(int paramInt1, int paramInt2);

    int glGetTexLevelParameteriv(int paramInt1, int paramInt2, int paramInt3);

    void glGetTexImage(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte);

    int glGetError();

    String glGetString(int paramInt);

    void glGetFloatv(int paramInt1, float[] paramArrayOfFloat, int paramInt2);

    void glGetIntegerv(int paramInt1, int[] paramArrayOfInt, int paramInt2);

    void glEnd();

    void glVertex2i(int x, int y);

    void glTexCoord2f(float u, float v);

    void glBegin(int mode);

    void glBindTexture(int target, int id);

    void glShaderSource(int paramInt, String paramString);

    void glDisable(int mode);

    void glEnable(int mode);

    void glBufferDataARBub(int target, int size, byte[] data, int offset, int length);

    void glBindBufferARB(int target, int id);

    void glDrawElements(int mode, int count, int type, long indices);

    void glVertexPointer(int size, int type, int stride, long pointer);

    void glTexCoordPointer(int size, int type, int stride, long pointer);

    void glVertex2f(float x, float y);

    void glTexImage2Df(int target, int level, int internal, int width, int height, int border, int format, int type, float[] buffer, int usage);

    void glTexImage2Dub(int target, int level, int internal, int width, int height, int border, int format, int type, byte[] buffer, int usage);

    void glTexCoord2i(int u, int v);

    void glCompressedTexImage2Dub(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, byte[] paramArrayOfByte, int paramInt8);

    void glTexImage2Di(int target, int level, int internal, int width, int height, int border, int format, int type, int[] buffer, int usage);

    void glScissor(int x, int i, int max, int max1);

    void glDrawPixelsi(int width, int height, int i, int glInt, int[] buffer, int usage);

    void glDrawPixelsub(int width, int height, int glRgb, int glUnsignedByte, byte[] buffer, int usage);

    void glGenTextures(int i, int[] t, int i1);

    void glTexParameteri(int glTexture2d, int glTextureMagFilter, int glLinear);

    void glPushMatrix();

    void glLoadIdentity();

    void glMatrixMode(int mode);

    void glColor3f(float i, float i1, float i2);

    void glColor4f(float i, float i1, float i2, float v);

    void glPopMatrix();

    void glTranslatef(float x, float y, float z);

    void glLoadMatrixf(float[] matrix, int usage);

    void glMultMatrixf(float[] matrix, int usage);

    void glRotatef(float by, float pitch, float yaw, float roll);

    void glMultiTexCoord2f(int target, float u, float v);

    void glFlush();

    void glFinish();

	void glBlendFunc(int glSrcAlpha, int glOneMinusSrcAlpha);

	void swapBuffers(long l);

}