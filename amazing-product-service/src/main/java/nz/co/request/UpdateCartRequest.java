package nz.co.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value="The request for updating product in the cart")
public class UpdateCartRequest extends CartRequest{
}
