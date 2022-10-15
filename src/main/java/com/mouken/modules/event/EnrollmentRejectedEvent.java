package com.mouken.modules.event;

import com.mouken.modules.event.domain.Enrollment;

public class EnrollmentRejectedEvent extends EnrollmentEvent {

    public EnrollmentRejectedEvent(Enrollment enrollment) {
        super(enrollment, "Enrollment Rejected.");
    }
}

