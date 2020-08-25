package org.npathai.command;

import java.util.List;

public interface Command {
    @Deprecated
    List<String> execute();

    default CommandResponse executeNew() {
        return new CommandResponse(execute());
    }
}
