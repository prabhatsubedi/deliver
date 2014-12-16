package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.model.Page;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/25/14
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ManagerService {
    public PaginationDto getActionLog(Page page) throws Exception;
}
