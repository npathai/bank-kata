package org.npathai.command;

import java.util.List;
import java.util.Objects;

public class CommandResponse {
    private List<String> response;

    public CommandResponse(String response) {
        this(List.of(response));
    }

    public CommandResponse(List<String> response) {
        this.response = response;
    }

    public boolean isEmpty() {
        return response.isEmpty();
    }

    public Iterable<String> lines() {
        return response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandResponse that = (CommandResponse) o;
        return Objects.equals(response, that.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(response);
    }
}
