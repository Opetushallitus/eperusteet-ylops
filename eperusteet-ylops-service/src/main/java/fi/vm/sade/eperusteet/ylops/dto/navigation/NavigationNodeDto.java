package fi.vm.sade.eperusteet.ylops.dto.navigation;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NavigationNodeDto {
    private Long id;
    private LokalisoituTekstiDto label;
    private NavigationType type;
    private Map<String, Object> meta = new HashMap<>();
    private List<NavigationNodeDto> children = new ArrayList<>();

    static public NavigationNodeDto of(NavigationType type, LokalisoituTekstiDto label, Long id) {
        NavigationNodeDto result = new NavigationNodeDto();
        result.setType(type);
        result.setLabel(label);
        result.setId(id);
        return result;
    }

    static public NavigationNodeDto of(NavigationType type) {
        return of(type, null, null);
    }

    public NavigationNodeDto meta(String key, Object value) {
        meta.put(key, value);
        return this;
    }

    public NavigationNodeDto add(NavigationNodeDto node) {
        if (node != null) {
            this.children.add(node);
        }
        return this;
    }

    public NavigationNodeDto addAll(Stream<NavigationNodeDto> nodes) {
        if (nodes != null) {
            this.children.addAll(nodes.collect(Collectors.toList()));
        }
        return this;
    }

    public NavigationNodeDto addAll(Collection<NavigationNodeDto> nodes) {
        if (nodes != null) {
            this.children.addAll(nodes);
        }
        return this;
    }

    public NavigationNodeDto addAll(NavigationNodeDto node) {
        if (node != null) {
            this.children.addAll(node.children);
        }
        return this;
    }
}
