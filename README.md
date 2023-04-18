# Anti-Fraud Service Hyperskill
The purpose of this project is to provide a service that detects fraudulent transactions using a set of predefined rules. The project includes a RESTful API that allows clients to submit transactions for analysis, as well as retrieve the status of previous transactions.

## Requirements
- Java 11 or higher
- Gradle 7.0 or higher
- DBMS

By default, this app uses H2 database which is an embedded database. However, if you want to use a different database management system (DBMS) like MySQL, you will need to configure the app to use it instead of H2. This can involve modifying the app's configuration files to specify the database driver, URL, username, and password for connecting to the MySQL database

### Transaction Validation

This Anti-fraud System has various mechanisms in place to prevent fraudulent money transfers from an account. Firstly, it employs heuristics rules to identify and prevent illegal transactions. Secondly, it maintains a block list of card numbers and IPv4 addresses that have been flagged for suspicious activity. Finally, the system utilizes a rule-based approach to correlate IP addresses and regions. For instance, if a transaction contains a card number, it is prohibited if there have been transactions from more than two regions of the world, other than the region of the current transaction, in the past hour. Additionally, if there have been transactions from more than two unique IP addresses, other than the IP of the current transaction, in the past hour, the transaction is also prohibited. However, if there have been transactions from two regions of the world and two unique IP addresses in the past hour, the transaction is sent for manual processing.


#### Region Codes:
| Code | Description                      |
|------|----------------------------------|
| EAP  | East Asia and Pacific            |
| ECA  | Europe and Central Asia          |
| HIC  | High-Income countries            |
| LAC  | Latin America and the Caribbean  |
| MENA | The Middle East and North Africa |
| SA   | South Asia                       |
| SSA  | Sub-Saharan Africa               |


### Authentication and Authorization

In the project system, there are varying levels of access granted to different types of users based on their roles. This is particularly relevant for enterprise applications such as anti-fraud systems, which are used by a range of users with different access requirements.

#### Role Model:
| Endpoint                        | Anonymous | Merchant | Administrator | Support |
|---------------------------------|-----------|----------|---------------|---------|
| POST /api/auth/user             | +         | +        | +             | +       |
| DELETE /api/auth/user           | -         | -        | +             | -       |
| GET /api/auth/list              | -         | -        | +             | +       |
| POST /api/antifraud/transaction | -         | +        | -             | -       |
| /api/antifraud/suspicious-ip    | -         | -        | -             | +       |
| /api/antifraud/stolencard       | -         | -        | -             | +       |
| GET /api/antifraud/history      | -         | -        | -             | +       |
| PUT /api/antifraud/transaction  | -         | -        | -             | +       |

(+) = authorized | (-) = unauthorized

### Feedback

An effective anti-fraud system must consider the dynamic transaction environment, which is influenced by various factors such as the economy, fraudster behavior, and transaction volume that define fraudulent activity.

The anti-fraud system integrates an adaptive feature known as "feedback," which requires manual input from a designated "SUPPORT" expert to evaluate completed transactions. The system will then use the feedback outcomes to adjust the fraud detection algorithm's limits according to predetermined rules. A table outlining the feedback system's logic is provided below.

#### Feedback system:
| Transaction Feedback → Transaction Validity ↓ | ALLOWED              | MANUAL_PROCESSING | PROHIBITED           |
|-----------------------------------------------|----------------------|-------------------|----------------------|
| ALLOWED                                       | Exception            | ↓ max ALLOWED     | ↓ max ALLOWED/MANUAL |
| MANUAL_PROCESSING                             | ↑ max ALLOWED        | Exception         | ↓ max MANUAL         |
| PROHIBITED                                    | ↑ max ALLOWED/MANUAL | ↑ max MANUAL      | Exception            |

#### Formula:
- increasing the limit: new_limit = 0.8 * current_limit + 0.2 * value_from_transaction
- decreasing the limit: new_limit = 0.8 * current_limit - 0.2 * value_from_transaction

## Usage

```bash
  ./gradlew bootRun 
```
