package openfoodfacts.github.scrachx.openfood.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lobster on 04.03.18.
 */

public class CountriesWrapper {

    private List<CountryResponse> countries;

    public List<Country> map() {
        List<Country> entityCountries = new ArrayList<>();
        for (CountryResponse country : countries) {
            entityCountries.add(country.map());
        }

        return entityCountries;
    }

    public List<CountryResponse> getCountries() {
        return countries;
    }

    public void setCountries(List<CountryResponse> countries) {
        this.countries = countries;
    }
}
