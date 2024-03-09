package Eco.TradeX.business.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TokenExceptions extends ResponseStatusException {
    public TokenExceptions(String errorMessage) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Token Error: " + errorMessage);
    }
}
