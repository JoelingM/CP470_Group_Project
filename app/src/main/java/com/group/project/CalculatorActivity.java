package com.group.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import java.util.Stack;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class CalculatorActivity extends AppCompatActivity {

    String result = "";

    /**
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        Toolbar toolbar = findViewById(R.id.cal_toolbar);
        setSupportActionBar(toolbar);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        //args.putInt("Position", position);

        CalculatorKeypadActivity keypadFrag = new CalculatorKeypadActivity(this);
        keypadFrag.setArguments(args);

        ft.add(R.id.fragmentFrameLayout, keypadFrag);
        ft.commit();

        Snackbar.make(findViewById(R.id.calcLayout), "Welcome", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    /**
     * {@inheritDoc}
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calculator_toolbar, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     * @param item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.jm_toolbar_about))
                .setMessage(getString(R.string.jm_aboutText))
                .setPositiveButton(R.string.jm_okText, null)
                .show();
        return true;
    }

    /**
     * Switch fragment in frameLayout to Variable pad fragment
     */
    public void switchToVarFrag() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        //args.putInt("Position", position);

        CalculatorVarpadActivity varpadFrag = new CalculatorVarpadActivity(this);
        varpadFrag.setArguments(args);

        ft.replace(R.id.fragmentFrameLayout, varpadFrag);
        ft.commit();

    }

    /**
     * Switch fragment in frameLayout to key pad fragment
     */
    public void switchToKeyFrag() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        //args.putInt("Position", position);

        CalculatorKeypadActivity keypadFrag = new CalculatorKeypadActivity(this);
        keypadFrag.setArguments(args);

        ft.replace(R.id.fragmentFrameLayout, keypadFrag);
        ft.commit();

    }

    /**
     * Switch fragment in frameLayout to saved equations fragment
     */
    public void switchToEqFrag() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        //args.putInt("Position", position);

        CalculatorEquationsActivity eqFrag = new CalculatorEquationsActivity(this);
        eqFrag.setArguments(args);

        ft.replace(R.id.fragmentFrameLayout, eqFrag);
        ft.commit();

    }


    /**
     * @param expression: the equation to be evaluated as a string
     * @return The result of the equation as a string
     */
    public String evaluate(String expression) {

        char[] tokens = expression.toCharArray();

        // Stack for numbers: 'values'
        Stack<Double> values = new Stack<Double>();

        // Stack for Operators: 'ops'
        Stack<Character> ops = new Stack<Character>();

        //True if there is already a decimal in the current number
        boolean decimalFlag = false;

        //True if the minus sign should be considered a negative sign
        //Begining of the equation or directly after a open bracket
        boolean negativeFlag = true;

        //Check for variables in the equation
        for (int j = 0; j < tokens.length; j++){
            if (tokens[j] == 'X' || tokens[j] == 'Y' || tokens[j] == 'Z' || tokens[j] == '\u03B8' || tokens[j] == '\u03D5') {
                return "Cannot calculate equation with unknown variables";
            }
        }

        //Check for equal number of opening and closing brackets
        int counter = 0;
        for (int j = 0; j < tokens.length; j++){
            if (tokens[j] == ')') {
                counter++;
            } else if (tokens[j] == '('){
                counter--;
            }
        }
        if (counter != 0){
            return "Invalid Brackets";
        }

        for (int i = 0; i < tokens.length; i++) {
            // Current token is a whitespace, skip it
            if (tokens[i] == ' ')
                continue;

            // Current token is a number, push it to stack for numbers

            if ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.' || (tokens[i] == '-' && negativeFlag)) {
                decimalFlag = false;
                negativeFlag = false;
                StringBuffer sbuf = new StringBuffer();
                sbuf.append(tokens[i]);
                if (tokens[i] == '.'){ decimalFlag = true; }
                //Check for multiple operators in a row


                if (tokens[i] == '-' && (tokens[i+1] == '+' || tokens[i+1] == '-' || tokens[i+1] == '*' || tokens[i+1] == '/' || tokens[i+1] == '^' || tokens[i+1] == '(' || tokens[i+1] == ')')){
                    return "Error: No number assigned to negative symbol.";
                }

                // There may be more than one digits in number
                while (i+1 < tokens.length && ((tokens[i+1] >= '0' && tokens[i+1] <= '9') || tokens[i+1] == '.' )) {
                    i++;
                    if (tokens[i] == '.' && decimalFlag) {
                        return "Invalid Syntax";
                    }
                    if (tokens[i] == '.') {
                        decimalFlag = true;
                    }
                    sbuf.append(tokens[i]);
                }
                values.push(Double.parseDouble(sbuf.toString()));
            }

            // Current token is an opening brace, push it to 'ops'
            else if (tokens[i] == '(') {
                ops.push(tokens[i]);
                if (tokens[i + 1] == '+' || tokens[i + 1] == '*' || tokens[i + 1] == '/' || tokens[i + 1] == '^') {
                    return "Error: Operator without value in front";
                }
                negativeFlag = true;

                // Closing brace encountered, solve entire brace
            } else if (tokens[i] == ')') {

                while (ops.peek() != '('){
                    values.push(Double.parseDouble(applyOp(ops.pop(), values.pop(), values.pop())));
                }

                ops.pop();
            }

            // Current token is an operator.
            else if (tokens[i] == '+' || tokens[i] == '-' ||
                    tokens[i] == '*' || tokens[i] == '/' || tokens[i] == '^') {

                //Check for close bracket after operator
                if (tokens[i+1] == ')'){
                    return "Error: Operator without value in front";
                }
                //Check for multiple operators in a row
                if (tokens[i+1] == '+' || tokens[i+1] == '-' || tokens[i+1] == '*' || tokens[i+1] == '/' || tokens[i+1] == '^'){
                    return "Error: Multiple Successive Operators";
                }
                // While top of 'ops' has same or greater precedence to current
                // token, which is an operator. Apply operator on top of 'ops'
                // to top two elements in values stack
                while (!ops.empty() && hasPrecedence(tokens[i], ops.peek())){
                    values.push(Double.parseDouble(applyOp(ops.pop(), values.pop(), values.pop())));
                }

                // Push current token to 'ops'.
                ops.push(tokens[i]);
            }

        }

        // Entire expression has been parsed at this point, apply remaining
        // ops to remaining values
        while (!ops.empty()){
            values.push(Double.parseDouble(applyOp(ops.pop(), values.pop(), values.pop())));
        }

        // Top of 'values' contains result, return it
        return values.pop().toString();
    }

    /**
     *
     * @param op1 - Operator 1 to check
     * @param op2 - Operator 2 to check
     * @return - Returns true if 'op2' has higher or same priority as 'op1', false otherwise.
     */
    public static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')')
            return false;
        if (op1 == '^' && (op2 == '*' || op2 == '/' || op2 == '+' || op2 == '-'))
            return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false;
        else
            return true;
    }

    // A utility method to apply an operator 'op' on operands 'a'
    // and 'b'. Return the result.
    public static String applyOp(char op, double b, double a) {

        switch (op) {
            case '+':
                return Double.toString(a + b);
            case '-':
                return Double.toString(a - b);
            case '*':
                return Double.toString(a * b);
            case '^':
                return Double.toString(Math.pow(a, b));
            case '/':
                if (b == 0)
                    return "Cannot Divide By 0";
                return Double.toString(a / b);
        }
        return "Unreconized Operator";
    }
}

