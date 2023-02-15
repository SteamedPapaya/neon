package com.mouken.modules.party.event;

import com.mouken.modules.party.event.domain.Enrollment;

public class EnrollmentRejectedEvent extends EnrollmentEvent {

    public EnrollmentRejectedEvent(Enrollment enrollment) {
        super(enrollment, "Enrollment Rejected.");
    }
}

