package ua.ivan909020.scheduler.core.service.worker.partitioning.policy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.ServiceInstance;

import ua.ivan909020.scheduler.core.configuration.properties.SchedulerPartitioningProperties;
import ua.ivan909020.scheduler.core.model.domain.instance.Instance;
import ua.ivan909020.scheduler.core.service.discovery.InstanceRegistry;
import ua.ivan909020.scheduler.core.testdata.ServiceInstanceMock;

@ExtendWith(MockitoExtension.class)
class PartitionPolicyDiscoveryTest {

    @Mock
    private SchedulerPartitioningProperties partitioningProperties;

    @Mock
    private InstanceRegistry instanceRegistry;

    @InjectMocks
    private PartitionPolicyDiscovery partitionPolicyDiscovery;

    @ParameterizedTest
    @MethodSource("computePartitions_shouldReturnPatritions_arguments")
    void computePartitions_shouldReturnPatritions(
            Instance currentInstance,
            List<Instance> allInstances,
            int maxPartitions,
            List<Integer> expectedPartitions) {

        doReturn(currentInstance).when(instanceRegistry).getCurrentInstance();
        doReturn(allInstances).when(instanceRegistry).getAllInstances();

        if (!allInstances.isEmpty()) {
            doReturn(maxPartitions).when(partitioningProperties).getMaxPartitions();
        }

        List<Integer> actualPartitions = partitionPolicyDiscovery.computePartitions();

        assertEquals(expectedPartitions, actualPartitions);
    }

    private static Stream<Arguments> computePartitions_shouldReturnPatritions_arguments() {
        Stream<Stream<Arguments>> streams = Stream.of(
                argumets_1(),
                argumets_2(),
                argumets_3(),
                argumets_4());

        return streams.flatMap(Function.identity());
    }

    private static Stream<Arguments> argumets_1() {
        List<ServiceInstance> allInstances = new ArrayList<>();

        int maxPartitions = 2;

        return Stream.of(
                Arguments.of(buildInstance("1"), allInstances, maxPartitions, List.of()));
    }

    private static Stream<Arguments> argumets_2() {
        List<Instance> allInstances = new ArrayList<>();
        allInstances.add(buildInstance("1"));
        allInstances.add(buildInstance("2"));
        allInstances.add(buildInstance("3"));
        Collections.shuffle(allInstances);

        int maxPartitions = 2;

        return Stream.of(
                Arguments.of(buildInstance("1"), allInstances, maxPartitions, List.of(0, 1)),
                Arguments.of(buildInstance("2"), allInstances, maxPartitions, List.of()),
                Arguments.of(buildInstance("3"), allInstances, maxPartitions, List.of()));
    }

    private static Stream<Arguments> argumets_3() {
        List<Instance> allInstances = new ArrayList<>();
        allInstances.add(buildInstance("1"));
        allInstances.add(buildInstance("2"));
        allInstances.add(buildInstance("3"));
        allInstances.add(buildInstance("4"));
        Collections.shuffle(allInstances);

        int maxPartitions = 10;

        return Stream.of(
                Arguments.of(buildInstance("1"), allInstances, maxPartitions, List.of(0, 1)),
                Arguments.of(buildInstance("2"), allInstances, maxPartitions, List.of(2, 3)),
                Arguments.of(buildInstance("3"), allInstances, maxPartitions, List.of(4, 5)),
                Arguments.of(buildInstance("4"), allInstances, maxPartitions, List.of(6, 7, 8, 9)));
    }

    private static Stream<Arguments> argumets_4() {
        List<Instance> allInstances = new ArrayList<>();
        allInstances.add(buildInstance("1"));
        allInstances.add(buildInstance("2"));
        allInstances.add(buildInstance("3"));
        allInstances.add(buildInstance("4"));
        allInstances.add(buildInstance("5"));
        Collections.shuffle(allInstances);

        int maxPartitions = 10;

        return Stream.of(
                Arguments.of(buildInstance("1"), allInstances, maxPartitions, List.of(0, 1)),
                Arguments.of(buildInstance("2"), allInstances, maxPartitions, List.of(2, 3)),
                Arguments.of(buildInstance("3"), allInstances, maxPartitions, List.of(4, 5)),
                Arguments.of(buildInstance("4"), allInstances, maxPartitions, List.of(6, 7)),
                Arguments.of(buildInstance("5"), allInstances, maxPartitions, List.of(8, 9)));
    }
    
    private static Instance buildInstance(String instanceId) {
        Instance instance = new Instance();
        instance.setServiceInstance(new ServiceInstanceMock(instanceId));
        return instance;
    }

}
