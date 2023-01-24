package nz.co.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value="The request for adding product to the cart")
public class UpdateCartRequest extends AddCartRequest{
}
