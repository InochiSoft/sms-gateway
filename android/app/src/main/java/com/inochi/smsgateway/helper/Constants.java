package com.inochi.smsgateway.helper;

public final class Constants {
    public static class Default {
        public static final String APP = "com.inochi.smsgateway";
        private static final int REQUEST = 2000;
    }

    public static final class Action {
        public static final String CLOSE_NOTIFY = Default.APP + ".action." + "CLOSE_NOTIFY";
        public static final String SHOW_NOTIFY = Default.APP + ".action." + "SHOW_NOTIFY";
        public static final String CREATE_DAILY = Default.APP + ".action." + "CREATE_DAILY";
        public static final String REMOVE_DAILY = Default.APP + ".action." + "REMOVE_DAILY";
        public static final String CHECK_OUTBOX = Default.APP + ".action." + "CHECK_OUTBOX";
    }

    public static final class Setting {
        public static final String BOOT_PERMISSION = Default.APP + ".setting." + "BOOT_PERMISSION";
        public static final String IP_SERVER = Default.APP + ".setting." + "IP_SERVER";
        public static final String CHECK_DURATION = Default.APP + ".setting." + "CHECK_DURATION";
        public static final String NOTIF_ID = Default.APP + ".setting." + "NOTIF_ID";
        public static final String NOTIF_TITLE = Default.APP + ".setting." + "NOTIF_TITLE";
        public static final String NOTIF_TEXT = Default.APP + ".setting." + "NOTIF_TEXT";
        public static final String NOTIF_ICON = Default.APP + ".setting." + "NOTIF_ICON";
    }

    public static final class Permission {
        public static final class Type {
            public static final int RECEIVE_BOOT_COMPLETED = Default.REQUEST + 1;
            public static final int WAKE_LOCK = Default.REQUEST + 2;
            public static final int VIBRATE = Default.REQUEST + 3;
            public static final int READ_SMS = Default.REQUEST + 4;
            public static final int RECEIVE_SMS = Default.REQUEST + 5;
            public static final int SEND_SMS = Default.REQUEST + 6;
        }
    }
}

