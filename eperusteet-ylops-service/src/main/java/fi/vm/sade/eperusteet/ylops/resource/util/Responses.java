package fi.vm.sade.eperusteet.ylops.resource.util;

import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Apuluokka tyypillisten vastausten luontiin.
 */
public final class Responses {

    private Responses() {
    }

    /**
     * Luo ResponseEntityn
     *
     * @param <T>  tyyppi
     * @param data body, voi oll null
     * @return jos data on null, asettaa paluukoodiksi NOT_FOUD, muussa tapauksessa OK
     */
    public static <T> ResponseEntity<T> ofNullable(T data) {
        if (data == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    public static <T> ResponseEntity<T> ofNullable(T data, HttpHeaders headers) {
        if (data == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    public static <T> ResponseEntity<T> of(Optional<T> data) {
        if (!data.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(data.get(), HttpStatus.OK);
    }
}
