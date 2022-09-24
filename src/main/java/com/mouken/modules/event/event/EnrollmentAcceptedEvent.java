package com.mouken.modules.event.event;

import com.mouken.modules.event.Enrollment;

public class EnrollmentAcceptedEvent extends EnrollmentEvent{

    public EnrollmentAcceptedEvent(Enrollment enrollment) {
        super(enrollment, "Enrollment accepted");
    }

}
