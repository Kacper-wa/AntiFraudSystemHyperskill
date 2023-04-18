package antifraud.entity.request;

import antifraud.constraints.RoleValid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Role {

    @NotNull
    private String username;

    @RoleValid
    private String role;
}
