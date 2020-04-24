package commons.route;

/**
 * com.fordeal.disney.commons.route
 *
 * @Project disney
 * @Author Moxiao 2019-06-03 11:15
 * @Use 主从分类
 */
public enum RouteDataSourceKeyEnum {
    /*promotion库*/
    MASTER, SLAVE,
    /* 新promotion库*/
    NEWPROMASTER,NEWPROSLAVE,
    /*旧优惠券系统库*/
    CHUANG_WAI, ITEM_CENTER
}
