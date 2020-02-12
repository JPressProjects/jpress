package io.jpress.web.front;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.model.UserAddress;
import io.jpress.service.UserAddressService;
import io.jpress.web.base.UcenterControllerBase;

import java.util.Set;


@RequestMapping(value = "/ucenter/address", viewPath = "/WEB-INF/views/ucenter/address")
public class AddressController extends UcenterControllerBase {

    @Inject
    private UserAddressService userAddressService;

    /**
     * 用户地址列表
     */
    public void index() {

        Page<UserAddress> page = userAddressService.paginate(getPagePara(), 10, getLoginedUser().getId());
        setAttr("page", page);
        render("address_list.html");
    }

    /**
     * 用户地址新增，编辑
     */
    public void edit() {
        Long id = getParaToLong("id");
        if (id != null) {
            UserAddress data = userAddressService.findById(id);
            render404If(notLoginedUserModel(data));
            setAttr("address", data);
        }
        render("address_edit.html");
    }

    /**
     * 选择地址的弹出层
     */
    public void layer() {
        Page<UserAddress> page = userAddressService.paginate(getPagePara(), 10, getLoginedUser().getId());
        setAttr("page", page);
        render("address_layer.html");
    }


    /**
     * 批量删除
     */
    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds() {
        Set<String> idsSet = getParaSet("ids");

        for (String id : idsSet) {
            UserAddress address = userAddressService.findById(id);
            if (address != null && isLoginedUserModel(address)){
                userAddressService.delete(address);
            }
        }

        renderJson(Ret.ok());
    }

    /**
     * 单个删除
     */
    public void doDel() {
        UserAddress address = userAddressService.findById(getIdPara());
        if (address != null && isLoginedUserModel(address)){
            userAddressService.delete(address);
        }
        renderOkJson();
    }

    /**
     * 新增/编辑地址
     */
    @EmptyValidate({
            @Form(name = "address.username",message = "请填写联系人"),
            @Form(name = "address.mobile",message = "请填写联系方式"),
            @Form(name = "address.detail",message = "请填写联系地址"),
    })
    public void doAdd() {
        UserAddress address = getBean(UserAddress.class, "address");
        userAddressService.addUserAddress(address,getLoginedUser().getId());
        renderJson(Ret.ok());
    }

}
