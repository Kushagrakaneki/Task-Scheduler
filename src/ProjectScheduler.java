import java.sql.*;
import java.util.*;

public class ProjectScheduler {


    static String URL = "jdbc:postgresql://localhost:5432/promanage_db";
    static String USER = "postgres";
    static String PASS = "kushagrakaneki";


    //project model
    static class Project {
        int id;
        String name;
        int deadline;
        double revenue;

        Project(int id, String name, int deadline, double revenue) {
            this.id = id;
            this.name = name;
            this.deadline = deadline;
            this.revenue = revenue;
        }
    }

    //main class
    public static void main(String[] args) {
        List<Project> projects = getProjects();

        if (projects.size() == 0) {
            System.out.println("No projects found.");
        } else {
            schedule(projects);
        }
    }


    static List<Project> getProjects() {
        List<Project> list = new ArrayList<>();

        try {
            //connecting to postgres
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM projects");

            while (rs.next()) {
                int id = rs.getInt("project_id");
                String title = rs.getString("title");
                int deadline = rs.getInt("deadline");
                double revenue = rs.getDouble("revenue");

                //adding data
                list.add(new Project(id, title, deadline, revenue));
            }

            conn.close();
        } catch (Exception e) {
            System.out.println("Database Error");
        }

        return list;
    }


    //schedule
    static void schedule(List<Project> projects) {

        //sorting in descedning order on basis of revenue
        projects.sort((p1, p2) -> Double.compare(p2.revenue, p1.revenue));

        Project[] week = new Project[5]; // Monday to Friday
        double total = 0;

        for (Project p : projects) {

            int lastDay = p.deadline - 1;
            if (lastDay > 4) {
                lastDay = 4;
            }

            for (int day = lastDay; day >= 0; day--) {
                if (week[day] == null) {
                    week[day] = p;
                    total += p.revenue;
                    break;
                }
            }
        }

        printSchedule(week, total);
    }


    //
    static void printSchedule(Project[] week, double total) {

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        System.out.println("\nWeekly Schedule:");
        for (int i = 0; i < 5; i++) {
            if (week[i] == null) {
                System.out.println(days[i] + " : No Project");
            } else {
                System.out.println(days[i] + " : " + week[i].name +
                        " ($" + week[i].revenue + ")");
            }
        }

        System.out.println("Total Revenue: $" + total);
    }
}
