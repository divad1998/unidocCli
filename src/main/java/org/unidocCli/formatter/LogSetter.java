package org.unidocCli.formatter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;

public class LogSetter {

    public Logger setLog(Logger log) {
        log.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        Formatter formatter = new LogFormatter();
        handler.setFormatter(formatter);
        log.addHandler(handler);
        return log;
    }
}
