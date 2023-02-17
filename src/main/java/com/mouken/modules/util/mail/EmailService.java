package com.mouken.modules.util.mail;

import org.springframework.stereotype.Service;

public interface EmailService {

    void sendEmail(EmailMessage emailMessage);
}
