package fr.ensibs.sondages.questions;

import net.jini.core.entry.Entry;

import java.lang.reflect.Field;
import java.util.Scanner;
import java.util.UUID;

public class DefaultQuestion<T extends Answer> implements Question, Entry {
    public UUID id;
    public String question;
    public Class<T> answer;

    public DefaultQuestion() {}

    public DefaultQuestion(String question, Class<T> answer) {
        this.id = UUID.randomUUID();
        this.question = question;
        this.answer = answer;
    }

    @Override
    public UUID getID() {
        return this.id;
    }

    @Override
    public String getQuestion() {
        return this.question;
    }

    @Override
    public T makeResponseForm() {
        T a = null;
        try {
            a = answer.getConstructor(UUID.class).newInstance(this.id);
            System.out.println("Answer to question ("+a.question_id+"): ");

            Scanner reader = new Scanner(System.in);
            for (Field f:a.getClass().getFields()) {
                if (f.get(a) != null)
                    continue;
                boolean filled = false;
                while (! filled) {
                    System.out.print(f.getName() + "<" + f.getType().getSimpleName() + ">: ");
                    try {
                        if (f.getType() == Integer.class) {
                            int nb = reader.nextInt();
                            if (nb < 0 || nb > 5)
                                continue;
                            f.set(a, nb);
                        } else if (f.getType() == String.class) {
                            f.set(a, reader.nextLine());
                        } else if (f.getType() == Boolean.class) {
                            f.set(a, reader.nextBoolean());
                        } else {
                            System.out.println("ignored !");
                        }
                        filled = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        filled = false;
                    }
                }
            }
            System.out.println("OK !");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
    }
}
