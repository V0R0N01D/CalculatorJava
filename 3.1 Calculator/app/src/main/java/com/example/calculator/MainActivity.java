package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    EditText mainEdit;

    // кнопки с отдельными обработчиками
    Button ravno, clear, deleteLastSimbol;

    ArrayList<String> operationInString = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainEdit = (EditText) findViewById(R.id.calculEdit);

        // кнопка удаления последнего символа
        deleteLastSimbol = (Button) findViewById(R.id.back);
        deleteLastSimbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainEdit.setText(mainEdit.getText().toString().toCharArray(), 0, mainEdit.length() - 1);
            }
        });

        // кнопка очищения
        clear = (Button) findViewById(R.id.ac);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainEdit.setText("");
            }
        });

        // кнопка равно
        ravno = (Button) findViewById(R.id.ravno);
        ravno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operationInString.clear();
                // парсинг строки, чтобы закинуть все числа (в т.ч точки и с 2+ цифрами в массив строк) + обратная польская нотация
                ParsStringToList(mainEdit.getText().toString().toCharArray());

                // решение примера
                GetAnswer();

                // вывод ответа
                WindrawAnswer();
            }
        });


    }

    // парсинг строки по символам и получение стринг массива (в первую очередь такая сложность, чтобы получить не цифры, а числа), сразу с обратной польской нотацией
    public void ParsStringToList(char[] stringForPars) {
        // для проверки предыдущий символ число или нет
        boolean previousSimbol = true;
        // для обратной польской записи
        Stack<String> stackSimbol = new Stack<String>();

        for (int i = 0; i < stringForPars.length; i++) {
            String lastInStack = "";
            if (stackSimbol.stream().count() != 0) {
                lastInStack = stackSimbol.peek();
            }
            switch (stringForPars[i]) {
                case ('+'):
                    if (lastInStack == "-" || lastInStack == "+" || lastInStack == "*" || lastInStack == "/" || lastInStack == "^") {
                        operationInString.add(stackSimbol.pop());
                    }
                    stackSimbol.push("+");
                    previousSimbol = true;
                    break;
                case ('-'):
                    if (lastInStack == "-" || lastInStack == "+" || lastInStack == "*" || lastInStack == "/" || lastInStack == "^") {
                        operationInString.add(stackSimbol.pop());
                    }
                    stackSimbol.push("-");
                    previousSimbol = true;
                    break;
                case ('*'):
                    if (lastInStack == "*" || lastInStack == "/" || lastInStack == "^") {
                        operationInString.add(stackSimbol.pop());
                    }
                    stackSimbol.push("*");
                    previousSimbol = true;
                    break;
                case ('/'):
                    if (lastInStack == "*" || lastInStack == "/" || lastInStack == "^") {
                        operationInString.add(stackSimbol.pop());
                    }
                    stackSimbol.push("/");
                    previousSimbol = true;
                    break;
                case ('^'):
                    if (lastInStack == "^") {
                        operationInString.add(stackSimbol.pop());
                    }
                    stackSimbol.push("^");
                    previousSimbol = true;
                    break;
                case ('('):
                    stackSimbol.push("(");
                    previousSimbol = true;
                    break;
                case (')'):
                    // проверка пока не будет другой скобки
                    boolean stopPriority = true;
                    // проверка, что будет открывающая скобка для данной закрывающей скобки
                    try {
                        // пройти все элементы в стеке, пока не найдется открывающая скобка
                        while (stopPriority) {
                            if (stackSimbol.peek() == "(") {
                                stackSimbol.pop();
                                stopPriority = false;
                            }
                            else {
                                operationInString.add(stackSimbol.pop());
                            }
                        }
                    }
                    catch (Exception ex) {
                        Toast except = Toast.makeText(this, "Не хватает скобок открывающих скобок!", Toast.LENGTH_LONG);
                        except.show();
                        break;
                    }
                    previousSimbol = true;
                    break;
                case (' '):
                    break;
                default:
                    if (previousSimbol) {
                        // если предыдущий символ не число, добавить данную цифру
                        operationInString.add(Character.toString(stringForPars[i]));
                        previousSimbol = false;
                    } else {
                        // если предыдующий символ число, получить ласт элемент и добавить к нему новую цифру
                        int idLastElem = operationInString.size() - 1;

                        String newLast = operationInString.get(idLastElem) + stringForPars[i];
                        operationInString.set(idLastElem, newLast);
                    }
            }
        }
        // после всего добавление из стека последних символов
        while (true) {
            if (stackSimbol.empty()) {
                break;
            }
            else {
                operationInString.add(stackSimbol.pop());
            }
        }
    }

    // решение примера, который лежит в листе operationInString
    public void GetAnswer() {
        Stack<String> result = new Stack<String>();

        for (int i = 0; i < operationInString.size(); i++) {
            String elem = operationInString.get(i);
            if (elem == "+" || elem == "-" || elem == "*" || elem == "/" || elem == "^") {
                // вдруг не хватает операндов
                try {
                    double secondOperand = Double.parseDouble(result.pop());
                    double firstOperand = Double.parseDouble(result.pop());
                    double res;

                    switch (elem) {
                        case ("+"):
                            res = firstOperand + secondOperand;
                            result.push(res + "");
                            break;
                        case ("-"):
                            res = firstOperand - secondOperand;
                            result.push(res + "");
                            break;
                        case ("*"):
                            res = firstOperand * secondOperand;
                            result.push(res + "");
                            break;
                        case ("/"):
                            res = firstOperand / secondOperand;
                            result.push(res + "");
                            break;
                        case ("^"):
                            res = Math.pow(firstOperand, secondOperand);
                            result.push(res + "");
                            break;
                    }
                }
                catch (EmptyStackException e) {
                    Toast except = Toast.makeText(this, "Не хватает операндов!", Toast.LENGTH_LONG);
                    except.show();
                }
            }
            else {
                result.push(elem);
            }
        }

        operationInString.clear();

        for (String ele : result) {
            operationInString.add(ele);
        }
    }

    public void WindrawAnswer() {
        String windrawData = new String();
        for (String elem : operationInString) {
            windrawData += elem;
        }

        mainEdit.setText(windrawData);
    }

    // обработка нажатия на остальные кнопки кнопку
    public void onNumberClick(View view){

        Button button = (Button)view;
        mainEdit.append(button.getText());
    }
}

