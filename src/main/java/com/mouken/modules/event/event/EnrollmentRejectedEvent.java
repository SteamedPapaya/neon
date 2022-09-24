package com.mouken.modules.event.event;

import com.mouken.modules.event.Enrollment;

public class EnrollmentRejectedEvent extends EnrollmentEvent {

    public EnrollmentRejectedEvent(Enrollment enrollment) {
        super(enrollment, "Enrollment Rejected.");
    }
}

