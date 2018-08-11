package ru.dobrotrener.reactiveexample;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class PersonCommand {

    public PersonCommand(Person person) {
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
    }

    private String firstName;
    private String lastName;

    public String sayMyName() {
        return "My name is " + firstName + " " + lastName + ".";
    }
}
