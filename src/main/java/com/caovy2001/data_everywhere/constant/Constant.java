package com.caovy2001.data_everywhere.constant;

public class Constant {
    public static final String common_response_status = "common_response_status";
    public static final String commonResponseStatus = "commonResponseStatus";
    public static final String prefixPathFile = "/api/file/?path=";

    public static class Paypal {
        public static final String CLIENT_ID = "Ach_PbGhn-5RR7F6KTqvSgAxvp4stM-xwXqFDl0-CAH-J3cTpRnUbyohkCbXpqAK1sIXiX-wTh80hTWw";
        public static final String CLIENT_SECRET = "EPWGV_lcw-DsRUn1aIalk_tPtaIFvv1_j_qeXj0oMCzS1gM-0gnmsvVdMbq6UqXhaprtIvlNwr3xQ0Ss";
        public static final String MODE = "sandbox";
        public static final String cancelUrl = "http://127.0.0.1:3000/payment/paypal/cancel";
        public static final String returnUrl = "http://127.0.0.1:3000/payment/paypal/review_payment";

    }

    public static class JedisPrefix {
        public static String COLON = ":";
        public static String userIdPrefix_ = "userIdPrefix_";

        public static class Paypal {
            public static String paymentIdPrefix_ = "paymentIdPrefix_";
        }

    }

    public static class SecretLoginKey {
        public static String CHATBOT = "CHATBOT_wr0gnmsvVdMprt3xQ0SsIvlNbq6UqXha";
    }

}
