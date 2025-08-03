package com.customeronboarding.admin.util;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.qr.*;

import java.util.Base64;

public class TOTPUtil {

    private static final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private static final CodeVerifier codeVerifier = new DefaultCodeVerifier(new DefaultCodeGenerator(),new SystemTimeProvider());
    private static final QrGenerator qrGenerator = new ZxingPngQrGenerator();

    public static String generateSecretKey() {
        return secretGenerator.generate();
    }

    public static String getQRImage(String secret, String username, String issuer) throws QrGenerationException {
        QrData data = new QrData.Builder()
                .label(username)
                .secret(secret)
                .issuer(issuer)
                .digits(6)
                .period(30)
                .build();

        byte[] imageData = qrGenerator.generate(data);
        return Base64.getEncoder().encodeToString(imageData);
    }

    public static boolean verifyCode(String secret, String code) {
        return codeVerifier.isValidCode(secret, code);
    }
}
