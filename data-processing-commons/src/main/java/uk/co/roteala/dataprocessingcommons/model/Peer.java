package uk.co.roteala.dataprocessingcommons.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Peer extends BaseModel{
    private String address;
    private Integer port;
    private boolean active;
    private long lastTimeSeen;
}
