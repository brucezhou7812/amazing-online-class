package nz.co.service;

import nz.co.model.AddressDO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2022-12-04
 */
public interface AddressService{
    AddressDO detail(Long id);
}
