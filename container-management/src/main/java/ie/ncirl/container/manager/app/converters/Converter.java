package ie.ncirl.container.manager.app.converters;

import ie.ncirl.container.manager.app.dto.DTO;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Every DTO <-> Model converter must implement this interface.
 * It follows the Converter pattern.
 * Ref: https://bulldogjob.com/articles/287-converter-pattern-in-java-8
 *
 * Every DTO class must implement the interface DTO for supporting the conversion
 * @param <D>
 * @param <M>
 */
public interface Converter<D extends DTO, M> {

    D from(M domain);

    M from(D dto);

    default List<D> fromDomainList(final Collection<M> entities) {
        return entities.stream()
                .map(this::from)
                .collect(Collectors.toList());
    }

    default List<M> fromDTOList(final Collection<D> dtos) {
        return dtos.stream()
                .map(this::from)
                .collect(Collectors.toList());
    }

}
