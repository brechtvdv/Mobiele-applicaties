package be.ugent.groep3.bikebuddy.beans;

import java.io.Serializable;

/**
 * Created by brechtvdv on 09/05/15.
 */
public class User implements Serializable {

    private String name;
    private int bonuspoints;
    private int ranking;

    public User(String name, int bonuspoints) {
        this.name = name;
        this.bonuspoints = bonuspoints;
        this.ranking = 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", bonuspoints=" + bonuspoints +
                ", ranking=" + ranking +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBonuspoints() {
        return bonuspoints;
    }

    public void setBonuspoints(int bonuspoints) {
        this.bonuspoints = bonuspoints;
    }


    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }
}
