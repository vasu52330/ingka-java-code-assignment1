# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**
- What cost allocation model is being used (direct vs indirect, fixed vs variable)?
- At what level of granularity should costs be tracked (warehouse, store, order, SKU, shipment)?
- How are shared costs such as utilities, IT platforms, and management overhead distributed?
- How is labor cost captured (time-based, activity-based, shift-based)?
- What systems provide source data, and how reliable and timely is that data?
- How are discrepancies or missing operational data handled?
- How consistent is historical cost data across warehouses and stores?
- How is cost visibility provided to finance and operational stakeholders?
- Are there audit, compliance, or regulatory requirements affecting cost allocation?

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**
- What are the primary cost drivers (labor, transport, inventory holding, returns)?
- How is service quality defined and measured (delivery time, accuracy, SLAs)?
- Which processes show the most inefficiencies or waste?
- Can automation, process redesign, or better system integration reduce costs?
- How do we quantify savings and calculate ROI for each initiative?
- How do we prioritize initiatives with competing business objectives?
- How do we pilot optimizations before full-scale rollout?
- What risks could negatively impact customer experience?
- How do we ensure cost savings are sustainable long term?

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
- Which financial systems are involved (ERP, General Ledger, Payroll, Procurement)?
- What system acts as the source of truth for each cost category?
- Which data requires real-time synchronization versus batch processing?
- How are reconciliation, validation, and error handling managed?
- What integration mechanisms are available (APIs, events, file-based)?
- How are security, access control, and data privacy ensured?
- How do we monitor integration health and failures?
- How are schema changes or system upgrades handled?
- How do finance teams verify and trust the integrated data?

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
- What forecasting horizon is required (monthly, quarterly, yearly)?
- Which cost drivers have the highest impact on future costs?
- How do seasonality, promotions, and demand variability affect forecasts?
- How often should forecasts and budgets be reviewed and updated?
- How is variance between forecasted and actual costs analyzed?
- Is scenario planning required (best case, worst case, expected case)?
- How are budgets approved, tracked, and adjusted during execution?
- How do operational forecasts align with financial planning cycles?
- How transparent and explainable should forecasts be for stakeholders?

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
- How do we distinguish historical costs of the old warehouse from the new one?
- What effective dates or identifiers are needed to separate cost attribution?
- How does reusing the Business Unit Code impact reporting and budgeting?
- How can historical cost data be used as a benchmark for the new warehouse?
- What risks arise if historical data is lost or incorrectly merged?
- How is reporting handled during the transition period?
- How do we ensure the new warehouse operates within expected cost limits?
- What governance and approval controls are required during replacement?
- How are finance and operations teams informed of the change?

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
