package com.mouken.modules.party.event;

import com.mouken.modules.party.event.domain.Enrollment;

public class EnrollmentAcceptedEvent extends EnrollmentEvent{

    public EnrollmentAcceptedEvent(Enrollment enrollment) {
        super(enrollment, "Enrollment accepted");
    }

}
