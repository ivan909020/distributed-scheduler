package ua.ivan909020.scheduler.rest.service.instance;

import java.util.List;

import ua.ivan909020.scheduler.rest.model.dto.instance.InstanceDto;

public interface InstanceService {

    List<InstanceDto> findAll();

}
