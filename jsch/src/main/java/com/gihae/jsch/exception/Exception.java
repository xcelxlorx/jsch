package com.gihae.jsch.exception;

public class Exception extends Throwable {

    public static class Exception400 extends RuntimeException {
        public Exception400(String message) {
            super(message);
        }
    }

    public static class Exception404 extends RuntimeException {
        public Exception404(String message) {
            super(message);
        }
    }

    public static class Exception500 extends RuntimeException {
        public Exception500(String message) {
            super(message);
        }
    }
}
