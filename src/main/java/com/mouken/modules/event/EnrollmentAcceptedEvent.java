package com.mouken.modules.event;

import com.mouken.modules.event.domain.Enrollment;

public class EnrollmentAcceptedEvent extends EnrollmentEvent{

    public EnrollmentAcceptedEvent(Enrollment enrollment) {
        super(enrollment, "Enrollment accepted");
    }

}
