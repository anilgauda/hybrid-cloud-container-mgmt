package ie.ncirl.container.manager.library.configurevm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Container {
    private String id;
    private VMModel server;
    private ApplicationModel application;
    private Integer memory;
    private Integer cpu;
   
}
