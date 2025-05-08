from abc import ABC, abstractmethod
import matplotlib.pyplot as plt
from datetime import datetime
import csv
from collections import defaultdict
import mysql.connector


class Functionality(ABC):
    @abstractmethod
    def split(self):
        pass

    def emi_calc(self):
        pass

    def sip_calc(self):
        pass


class Tools(Functionality):
    def split(self):
        try:
            grp_size = int(input("Enter the group size: "))
            members = []
            if grp_size <= 0:
                print("Invalid group size")
                return
            for i in range(grp_size):
                member = input("Enter the name of the member: ")
                members.append(member)
            expense = int(input("Enter the total amount: "))
            description = input("Enter the description: ")
            for i in range(grp_size):
                print(
                    "Press",
                    (i),
                    "to add",
                    members[i],
                    "as the person who paid the bill",
                )
            paid_by = int(input("Enter the index:"))
            if paid_by >= 0 and paid_by < grp_size:
                expense_per_person = expense / grp_size
                print("Expense per person: ", expense_per_person)
                print(members[paid_by], "paid the bill")
                print(
                    "Others owe", members[paid_by], ":", (expense - expense_per_person)
                )
                print("For:", description)
            else:
                print("Invalid Index")
                return
        except Exception as e:
            print(e)
            return

    def emi_calc(self):
        try:
            amount = int(input("Enter the loan amount: "))
            rate = float(input("Enter the rate of interest: "))
            time = int(input("Enter the time in months: "))
            rate = rate / (1200)
            if rate == 0:
                print("EMI: , amount / time")
            emi = (amount * rate * (1 + rate) ** time) / ((1 + rate) ** time - 1)
            print("EMI: ", emi)
        except Exception as e:
            print(e)
            return

    def sip_calc(self):
        try:
            sip_amount = float(input("Enter Monthly SIP Amount: "))
            rate = float(input("Enter Annual Interest Rate (%): "))
            years = int(input("Enter Investment Duration (Years): "))
            n = years * 12
            r = (rate / 100) / 12
            sip_return = sip_amount * ((1 + r) ** n - 1) / r * (1 + r)
            print("Return is:", sip_return)
        except Exception as e:
            print(e)
            return


class User:
    def __init__(self):
        self.username = ""
        self.password = ""
        self.dob = ""
        self.conn = mysql.connector.connect(
            host="localhost",
            user="root",  # Default XAMPP user (change if needed)
            password="",  # Default XAMPP has no password
            database="UserData",
        )
        self.cursor = self.conn.cursor()

    def login(self):
        name = input("Enter your username: ")
        pwd = input("Enter your password: ")
        query = "SELECT password FROM userinfo WHERE username = %s"

        self.cursor.execute(query, (name,))
        result = self.cursor.fetchone()

        if result:
            stored_password = result[0]
            if pwd == stored_password:
                print("Login successful!")
                self.username = name
                self.password = pwd
                return True
            else:
                print("Incorrect password.")
                return False
        else:
            print("User not found.")
            return False

    def register(self):
        try:
            if len(self.username) > 0 and len(self.password) > 6:
                print("User already registered")
                return

            self.username = input("Enter the username: ")
            password = input("Enter the password: ")
            if len(password) < 6:
                print("Password should be atleast 6 characters")
                return
            self.password = password
            while True:
                date_str = input("Enter the date (YYYY-MM-DD): ")
                try:
                    self.dob = datetime.strptime(date_str, "%Y-%m-%d").date()
                    break
                except ValueError:
                    print("Invalid date format! Please enter in YYYY-MM-DD format.")
            query = "INSERT INTO userinfo (username, password, dob) VALUES (%s, %s, %s)"
            self.cursor.execute(query, (self.username, self.password, self.dob))
            self.conn.commit()
            print("User registered successfully")
            return
        except Exception as e:
            print(e)
            return

    def get_username(self):
        return self.username

    def get_password(self):
        return self.password

    def forgot_password(self):
        try:
            name = input("Enter username")
            if name == self.username:
                pwd = input("Enter new password")
                self.password = pwd
                print("Password updated successfully")
            else:
                print("Invalid username")
        except Exception as e:
            print(e)
            return


class Expenses:
    def __init__(self, user):
        self.transaction = []
        self.category = [
            "🍔 Food",
            "🚗 Travel",
            "👕 Shopping",
            "🏠 Rent",
            "📕 Education",
            "🎬 Entertainment",
            "💵 Investment",
            "Others",
        ]
        self.incomecat = ["Salary", "Business", "Stock", "Others"]
        self.budget = 500
        self.filename = "transactions.csv"
        self.conn = mysql.connector.connect(
            host="localhost",
            user="root",  # Default XAMPP user (change if needed)
            password="",  # Default XAMPP has no password
            database="UserData",
        )
        self.username = user.username
        self.cursor = self.conn.cursor()
        query = """
        SELECT balance 
        FROM transactions 
        WHERE username = %s 
        ORDER BY date DESC, amount DESC 
        LIMIT 1;
        """
        self.cursor.execute(query, (self.username,))
        result = self.cursor.fetchone()
        if result:
            self.balance = result[0]
        else:
            self.balance = 400
        self.load_transactions()

    def add_transaction(self):
        try:
            type = int(input("Press 1 for income and 2 for expense: "))
            if type != 1 and type != 2:
                print("Invalid choice")
                return
            if type == 1:
                for items in self.incomecat:
                    print("Press", (self.incomecat.index(items) + 1), "for", items)
                cat = int(input("Enter the category: "))
                if cat > 0 and cat < len(self.incomecat):
                    amount = int(input("Enter the amount: "))

                    while True:
                        date_str = input("Enter the date (YYYY-MM-DD): ")
                        try:
                            date1 = datetime.strptime(date_str, "%Y-%m-%d").date()
                            break
                        except ValueError:
                            print(
                                "Invalid date format! Please enter in YYYY-MM-DD format."
                            )
                    description = input("Enter the description: ")
                    self.balance += amount
                    self.transaction.append(
                        {
                            "Category": self.incomecat[cat - 1],
                            "Amount": amount,
                            "Type": "Income",
                            "Date": date1,
                            "Description": description,
                        }
                    )
                    sql = """INSERT INTO transactions (username, category, amount, type, date, description, balance) 
                         VALUES (%s, %s, %s, %s, %s, %s, %s)"""
                    values = (
                        self.username,
                        self.incomecat[cat - 1],
                        amount,
                        "Income",
                        date1,
                        description,
                        self.balance,
                    )

                    self.cursor.execute(sql, values)
                    self.conn.commit()
                    self.load_transactions()
                    self.save_transactions()
                else:
                    print("Invalid category")
                    return
            elif type == 2:
                amount = int(input("Enter the amount: "))
                if amount > self.balance:
                    print("Insufficient balance")
                    return
                while True:
                    date_str = input("Enter the date (YYYY-MM-DD): ")
                    try:
                        date2 = datetime.strptime(date_str, "%Y-%m-%d").date()
                        break
                    except ValueError:
                        print("Invalid date format! Please enter in YYYY-MM-DD format.")
                if self.monthly_summary(date2) >= self.budget:
                    choose = input(
                        "Budget is reached do you wish to continue ? Y or N: "
                    )
                    if choose == "Y":
                        pass
                    else:
                        return
                description = input("Enter the description: ")

                for items in self.category:
                    print("Press", (self.category.index(items) + 1), "for", items)
                cat = int(input("Enter the category: "))
                if cat > 0 and cat < len(self.category):
                    self.balance -= amount
                    self.transaction.append(
                        {
                            "Category": self.category[cat - 1],
                            "Amount": amount,
                            "Type": "Expense",
                            "Date": date2,
                            "Description": description,
                        }
                    )
                    sql = """INSERT INTO transactions (username, category, amount, type, date, description, balance) 
                         VALUES (%s, %s, %s, %s, %s, %s, %s)"""
                    values = (
                        self.username,
                        self.category[cat - 1],
                        amount,
                        "Expense",
                        date2,
                        description,
                        self.balance,
                    )
                    self.cursor.execute(sql, values)
                    self.conn.commit()
                    self.load_transactions()
                    self.save_transactions()
                else:
                    print("Invalid category")
                    return
            else:
                print("Invalid choice")
                return
        except Exception as e:
            print(e)
            return

    def get_balance(self):
        if self.balance == 0:
            return "Balance Empty Add income"
        return self.balance

    def display_transactions(self):
        if not self.transaction:
            print("No transactions to display")
            return
        for items in self.transaction:
            print(items)

    def delete_transaction(self):
        try:
            sql = """SELECT category, amount, type, date, description, balance 
                    FROM transactions WHERE username = %s"""
            self.cursor.execute(sql, (self.username,))
            rows = self.cursor.fetchall()

            if not rows:
                print("No transactions found.")
                return

            for i, row in enumerate(rows, start=1):
                print(f"{i}. {row}")

            index = int(input("Enter the index to delete: "))
            if index < 1 or index > len(rows):
                print("Invalid index")
                return

            transaction_to_delete = rows[index - 1]

            delete_sql = """DELETE FROM transactions 
                            WHERE username = %s AND category = %s AND amount = %s 
                            AND type = %s AND date = %s AND description = %s AND balance = %s
                            LIMIT 1"""
            self.cursor.execute(delete_sql, (self.username, *transaction_to_delete))
            self.conn.commit()

            print("Transaction deleted successfully")
            self.load_transactions()
            self.save_transactions()
        except Exception as e:
            print(f"Error: {e}")
            return

    def set_budget(self):
        try:
            self.budget = int(input("Enter Your Budget: "))
        except Exception as e:
            print(e)
            return

    def monthly_summary(self, date):
        total = sum(
            float(t["Amount"])
            for t in self.transaction
            if t["Date"].month == date.month and t["Date"].year == date.year
        )
        return total

    def load_transactions(self):
        try:
            sql = """SELECT category, amount, type, date, description, balance 
                    FROM transactions WHERE username = %s"""
            self.cursor.execute(sql, (self.username,))
            rows = self.cursor.fetchall()
            self.transaction.clear()
            self.transaction = [
                {
                    "Category": row[0],
                    "Amount": row[1],
                    "Type": row[2],
                    "Date": row[3],  # Already in DATE format from the database
                    "Description": row[4],
                }
                for row in rows
            ]
        except Exception as e:
            print(f"Error: {e}")
            self.transaction = []

    def save_transactions(self):
        with open(self.filename, mode="w", newline="") as file:
            fieldnames = ["Category", "Amount", "Type", "Date", "Description"]
            writer = csv.DictWriter(file, fieldnames=fieldnames)
            writer.writeheader()
            writer.writerows(self.transaction)

    def show_expense_pie_chart(self):
        category_expenses = defaultdict(float)

        for transaction in self.transaction:
            if transaction["Type"] == "Expense":
                category_expenses[transaction["Category"]] += float(
                    transaction["Amount"]
                )

        if not category_expenses:
            print("No expenses to display.")
            return

        labels = list(category_expenses.keys())
        sizes = list(category_expenses.values())

        plt.figure(figsize=(8, 6))
        plt.pie(
            sizes,
            labels=labels,
            autopct="%1.1f%%",
            startangle=140,
            colors=plt.cm.Paired.colors,
        )
        plt.title("Expense Distribution by Category")
        plt.axis("equal")
        plt.show()


if __name__ == "__main__":
    try:
        user = User()
        tools = Tools()
        expenses = Expenses(user)
        print("Welcome to Personal Finance Manager")
        flag = False
        while True:
            login = int(input("Press 1 to login and 2 to register: "))
            if login == 1:
                flag = user.login()
                if flag:
                    break
            elif login == 2:
                user.register()
            else:
                print("Invalid choice")
                exit()
        if flag:
            while True:
                print("Press 1 to view transaction menu")
                print("Press 2 to view tools")
                print("Press 3 to log out")
                choice = int(input("Enter your input: "))
                if choice == 1:
                    while True:
                        print("Press 1 to add transaction")
                        print("Press 2 to view transactions")
                        print("Press 3 to delete transactions")
                        print("Press 4 to set a budget limit")
                        print("Press 5 to show balance")
                        print("Press 6 to show statistics")
                        print("Press 7 to go back")
                        choice2 = int(input("Enter your input: "))
                        if choice2 == 1:
                            expenses.add_transaction()
                        elif choice2 == 2:
                            expenses.display_transactions()
                        elif choice2 == 3:
                            expenses.delete_transaction()
                        elif choice2 == 4:
                            expenses.set_budget()
                        elif choice2 == 5:
                            print(expenses.get_balance())
                        elif choice2 == 6:
                            expenses.show_expense_pie_chart()
                        elif choice2 == 7:
                            break
                        else:
                            print("Invalid input")
                            break
                elif choice == 2:
                    while True:
                        print("Press 1 to use split calculator")
                        print("Press 2 to use EMI calculator")
                        print("Press 3 to use SIP calculator")
                        print("Press 4 to go back")
                        choice3 = int(input("Enter your input: "))
                        if choice3 == 1:
                            tools.split()
                        elif choice3 == 2:
                            tools.emi_calc()
                        elif choice3 == 3:
                            tools.sip_calc()
                        elif choice3 == 4:
                            break
                        else:
                            print("Invalid Input")
                            break
                elif choice == 3:
                    print("Logged out successfully")
                    break
                else:
                    print("Invalid choice")
                    break
        else:
            print("Invalid credentials")
            exit()
    except Exception as e:
        print(e)
        exit()
