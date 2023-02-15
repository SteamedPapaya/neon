package com.mouken.modules.party.event;

import com.mouken.modules.party.event.domain.Enrollment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class EnrollmentEvent {

    protected final Enrollment enrollment;

    protected final String message;

}