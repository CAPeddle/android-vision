package com.google.android.gms.samples.vision.face.facetracker;

public class Constants {
	public interface ACTION {
		public static String MAIN_ACTION  = "com.truiton.foregroundservice.action.main";
		public static String PREV_ACTION  = "com.truiton.foregroundservice.action.prev";
		public static String PLAY_ACTION  = "com.truiton.foregroundservice.action.play";
		public static String NEXT_ACTION  = "com.truiton.foregroundservice.action.next";
		public static String PAUSE_ACTION = "com.truiton.foregroundservice.action.pause";
		public static String STARTFOREGROUND_ACTION = "com.truiton.foregroundservice.action.startforeground";
		public static String STOPFOREGROUND_ACTION = "com.truiton.foregroundservice.action.stopforeground";
	}

	public interface CAMERA {
		public static int WIDTH = 640;
		public static int HEIGHT = 480;
	}
	public interface NOTIFICATION_ID {
		public static int FOREGROUND_SERVICE = 101;
	}

	public interface PERMISSIONS {
			public static int REQUEST_CAMERA_PERMISSION = 200;
			public static int REQUEST_STORAGE_PERMISSION = 199;
	}
}
