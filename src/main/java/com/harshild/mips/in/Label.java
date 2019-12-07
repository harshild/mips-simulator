package com.harshild.mips.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Label {
    int startInstructionIndex;
    int endInstructionIndex;
    private String name;

    public Label(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Label label = (Label) o;
        return name.equals(label.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
