package org.svan.aalogger;

interface TestLogger extends AnnotatedMethodLogger {
    void setPlainLogger(PlainLoggingToErrorLog pl);
}
