package com.example.sms.domain;

import com.example.sms.domain.base.UpdatableEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity(name = "teacher")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends UpdatableEntity {

    private String firstName;

    private String lastName;

    private String subject;

    private String graduatedFrom;

}
