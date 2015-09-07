package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.ActionLogEntity;
import com.yetistep.delivr.model.Page;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/2/14
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ActionLogDaoService extends GenericDaoService<Long, ActionLogEntity> {
     public List<ActionLogEntity> findActionLogPaginated(Page page) throws Exception;

     public void saveAll(List<ActionLogEntity> values) throws Exception;

     public Integer getTotalNumberOfActionLogs() throws Exception;
}
