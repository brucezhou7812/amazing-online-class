package nz.co.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nz.co.enums.BizCodeEnum;
import nz.co.request.UserLoginRequest;
import nz.co.request.UserRegisterRequest;
import nz.co.request.UserTokenRefreshRequest;
import nz.co.service.FileService;
import nz.co.service.UserService;
import nz.co.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.spring.web.json.Json;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bruce Zhou
 * @since 2022-12-04
 */
@RestController
@RequestMapping("/api/user/v1")
@Api(tags = "User Service Module")
public class UserController {
    @Autowired
    private FileService fileService;
    @Autowired
    private UserService userService;
    @PostMapping(value="upload_user_img")
    @ApiOperation("Upload user image")
    public JsonData uploadUserImage(@RequestPart(value="file")
            @ApiParam(value="file",required = true) MultipartFile file){
        String result = fileService.uploadUserImage(file);
        return result!=null?JsonData.buildSuccess(result):JsonData.buildResult(BizCodeEnum.FILE_UPLOAD_USER_IMAGE_FAIL);
    }
    @PostMapping(value = "register")
    @ApiOperation("User reigister")
    public JsonData register(@RequestBody @ApiParam(value="User register Request",required = true) UserRegisterRequest userRegisterRequest){
        return userService.register(userRegisterRequest);
    }
    @GetMapping(value="login")
    @ApiOperation("User Login")
    public JsonData login(@RequestBody @ApiParam(value = "User Login Request",required = true) UserLoginRequest userLoginRequest){

        return userService.login(userLoginRequest);

    }
    @GetMapping(value="refresh")
    @ApiOperation("Refresh token")
    public JsonData refresh(@RequestBody @ApiParam(value = "User Token Request",required = true) UserTokenRefreshRequest userTokenRefreshRequest){
        return userService.refresh(userTokenRefreshRequest);
    }
}

