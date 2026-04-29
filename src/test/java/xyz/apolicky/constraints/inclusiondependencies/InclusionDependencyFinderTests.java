package xyz.apolicky.constraints.inclusiondependencies;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import scala.Tuple4;
import xyz.apolicky.constraints.inclusiondependencies.model.ImportantColumnsForInclusionsDependencies;
import xyz.apolicky.constraints.inclusiondependencies.model.MyInclusionDependency;
import xyz.apolicky.constraints.inclusiondependencies.pairings.InclusionDepBasedRowPairer;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class InclusionDependencyFinderTests {

    @Test
    public void getDatasetNameToInclusionDependencyRelatedColumnNames() {
        var inclusionDependencies = prepareInclusionDependencies();

        var result = ImportantColumnsForInclusionsDependencies.fromInclusionDependencies(inclusionDependencies);
        System.out.println(result);
//        TODO: jak se vubec testuje v Jave dpč
    }

    @Test
    public void getInclusionDependencyPairForGivenColumn() {
        var inclusionDependencies = prepareInclusionDependencies();
        var result1 = InclusionDepBasedRowPairer.getInclusionDependencyMappingsForGivenColumnAndDataset("Table1", "ColumnA", inclusionDependencies);
        var result2 = InclusionDepBasedRowPairer.getInclusionDependencyMappingsForGivenColumnAndDataset("Table1", "ColumnB", inclusionDependencies);
        System.out.println(result1);
        System.out.println(result2);
    }

    private List<MyInclusionDependency> prepareInclusionDependencies() {
        List<Tuple4<String, String, String, String>> tableColumnIds = List.of(
                new Tuple4<>("Table1", "ColumnA", "Table2", "ColumnX"),
                new Tuple4<>("Table2", "ColumnY", "Table1", "ColumnB"),
                new Tuple4<>("Table1", "ColumnC", "Table2", "ColumnZ")
        );

        List<MyInclusionDependency> inclusionDependencies = new ArrayList<>();

        for (var ind : tableColumnIds) {
            var id = new MyInclusionDependency(ind._2(), ind._1(), ind._4(), ind._3());
            inclusionDependencies.add(id);
        }
        return inclusionDependencies;
    }
}
