package fi.vm.sade.eperusteet.ylops.dto.ops;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OpsSisaltoQuery {
    private String query = "";
}
