package fr.bellingard.accountmanager.kmymoney;

import fr.bellingard.accountmanager.model.Repository;
import fr.bellingard.accountmanager.model.export.JSONExporter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class OtherKMyMoneyReaderTest {

    private static Repository repository;

    @BeforeClass
    public static void prepareFile() throws Exception {
        Path file = Paths.get("/Users/bellingard/AccountPersonalApp/some-tests/Comptes.kmy");
        repository = new Repository();
        KMyMoneyReader.on(file).populate(repository);

        dumpStructure(repository);
        dumpBankAccountBalance(repository);
    }

    @Test
    public void should_read_institutions() throws Exception {
        assertThat(repository.getInstitutions().size()).isEqualTo(4);
    }

    @Test
    public void should_read_payees() throws Exception {
        assertThat(repository.getPayees().size()).isEqualTo(500);
    }

    @Test
    public void should_read_accounts() throws Exception {
        assertThat(repository.getBankAccounts().size() + repository.getCategories().size()).isEqualTo(228);
    }

    @Test
    public void should_read_transactions() throws Exception {
        int numberOfBankTransactions = repository.getBankAccounts().stream()
                .mapToInt(a -> a.listTransactions().size())
                .sum();
        int numberOfCategoryTransactions = repository.getCategories().stream()
                .mapToInt(a -> a.listTransactions().size())
                .sum();

        assertThat(numberOfBankTransactions + numberOfCategoryTransactions).isEqualTo(4542 * 2);
    }

    private static void dumpStructure(Repository repository) {
        print(JSONExporter.export(repository));
    }

    private static void dumpBankAccountBalance(Repository repository) {
        repository.getBankAccounts().stream()
                .forEach(a -> print(a.getId() + " / " + a.getName() + " => " + (a.getBalance().floatValue() / 100)));
    }

    private static void print(Object a) {
        System.out.println(a);
    }
}