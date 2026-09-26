## 1. Why Java Exists (Portability, Simplicity, Security)


### 1. The Era Before Java (1980s–90s)

- Dominant languages: **C** and its successor **C++**.
- Why they won: fast, relatively simple (vs assembly), and **low-level** (close to hardware, few abstraction layers).
- "Low-level" = minimal abstraction → you manage memory, you talk almost directly to the OS/processor → fast but everything is manual.

**Historical language ladder (increasing abstraction):**

```
Machine Code (0s/1s) → Assembly (mnemonics: MOV, ADD, JMP) → C/C++ (English-like syntax) → Java/Python (heavy abstraction)
```

A **compiler** = software that converts source code (any HLL) → machine code (sequence of 0s and 1s that the target CPU understands).



### 2. The Core Problem: Portability

**Definition:** Portability = same source code runs unmodified across different machines. C/C++ did **not** have this — they are **platform-dependent**.

#### What is a "Platform"?

```
Platform = Processor (CPU architecture) + Operating System
```

Examples:
| Platform | Processor | OS |
|-|-|-|
| P1 | Intel x86 | Windows |
| P2 | ARM | macOS |

Same `hello.cpp` compiled for P1 and P2 → **two different binaries**. Neither binary runs on the other platform. Why? Two independent reasons:

#### Reason A — OS Differences

- Any I/O op (print to console, file read/write, memory allocation) is not something your program does itself — it's a **system call** delegated to the OS.
- The compiler bakes in calls to the OS's **system libraries** (compiled machine code) to fulfill these.
- Windows and macOS expose *different* system libraries/APIs for the same logical operation (e.g., "write to console").
- Result: even identical source → different injected library calls → different binary per OS.

#### Reason B — Processor (ISA) Differences

- A processor = physical hardware = **billions/trillions of transistors**, each simply ON (1, current flows) or OFF (0, no current).
- Vendors (Intel, ARM, etc.) design their **pin layouts, transistor arrangement, and instruction handling** differently.
- The contract between your compiled program and the processor is the **ISA — Instruction Set Architecture**.
  - ISA defines primitive ops: `ADD`, `LOAD`, `STORE`, `JUMP`, etc., and how they map onto that specific hardware.
  - Think of ISA as the **"grammar"** a processor understands. Intel and ARM speak different grammars.
- Same logical operation ("add two numbers") → different low-level binary encoding per ISA.

> **Interview-gold detail:** Most people say "C++ is platform-dependent" without being able to explain *why*. The real reason = (different OS system libraries baked into binary) + (different ISA per processor family). Both cause divergent machine code from identical source.

#### Net Effect

```
Same hello.cpp
   ├── compiled for P1 (x86 + Windows) → Binary A
   └── compiled for P2 (ARM + macOS)   → Binary B     (A ≠ B)
```

Every new platform → recompile from scratch. This is the **portability problem**.



### 3. Java's Fix: Bytecode + JVM

#### The Analogy
- You (the programmer) only know one language (say, Hindi/English) — this is your **source code**.
- Instead of learning every country's language (= compiling separately per platform, like C/C++ did), you travel with a **translator friend** who knows every local language.
- That translator = the **JVM**.

#### The Actual Mechanism

```
hello.java  --[javac compiler]-->  hello.class (BYTECODE)  --[JVM]-->  Platform-specific machine code
```

1. Source (`.java`) is compiled by `javac` into an **intermediate form**: **bytecode** (`.class` file) — NOT machine code, NOT 0s/1s tied to any CPU.
2. Bytecode is **identical** regardless of target platform. Compile once.
3. At runtime, the **JVM** (installed per platform) reads the bytecode and converts it, instruction by instruction, into that platform's native machine code.

```
                 ┌────────────┐
 hello.java > │   javac    │ > hello.class (bytecode — platform-independent)
                 └────────────┘
                                        │
                     ┌──────────────────┼──────────────────┐
                     ▼                                      ▼
              JVM (Windows/x86)                     JVM (macOS/ARM)
                     │                                      │
                     ▼                                      ▼
            Machine Code for P1                    Machine Code for P2
```

#### Critical Correction (commonly misunderstood)

> The JVM itself is **NOT** platform-independent — it is **platform-DEPENDENT** (there's a separate JVM binary per platform, e.g., JVM-for-Windows-x86, JVM-for-macOS-ARM).
> **Only the bytecode is platform-independent.**

So it's really: *one translator per country*, not one universal translator physically teleporting around.

```
Requirement: JVM must be installed on EVERY target platform.
Trade-off Java accepted: "write the JVM once per platform" so that
                          "you write your app once, ever."
```

#### WORA — Write Once, Run Anywhere

This is the marketing shorthand for: compile source → bytecode once; that bytecode runs on any platform that has a compatible JVM installed.

#### Why Portability Mattered So Much (business/historical context)

1. **Explosion of new device types** in the 90s: set-top boxes, TVs, embedded systems, and later — the modern parallel is IoT/smart devices. One app, many devices, without per-device recompilation.
2. **Speed stopped being the bottleneck.** Processors got fast enough that raw hardware efficiency (C/C++'s core selling point) mattered less than developer productivity and portability. (Same reason Python survives today despite being slower than Java.)
3. **Rise of the internet / client-server model.** Backend code deployed across many servers, each potentially a different platform (different OS/processor combos). Without portability, you'd recompile your backend app per server platform — a nightmare at scale.



### 4. Java is Simple

C/C++ had complexity sources that Java deliberately removed:
- **Pointers** (explicit memory addresses)
- **Multiple inheritance** (diamond problem)
- **Manual memory management** — specifically **manual deallocation** (allocation is unavoidable in any language, but explicit `free`/`delete` was a major source of bugs — dangling pointers, memory leaks, double-free)

Java abstracted these away (pointers → references + no pointer arithmetic; multiple inheritance → interfaces; manual dealloc → Garbage Collector). *How* each is solved is covered in later lectures — this lecture only frames *why* the removal was needed.



### 5. Java is Secure — The Sandbox Model

#### How Java's use cases exploded (and created risk)

- **Backend:** Java Servlets — server-side programs acting as APIs (fetch/insert DB data, etc.)
- **Frontend (historical):** Java **Applets** — lightweight bytecode transmitted over the internet, downloaded via a browser link, and executed **inside the client's browser**.
  - Fun fact: JavaScript was literally named after Java (riding on Java's popularity at the time) — despite having no real technical relationship to it.

#### The Risk

If arbitrary bytecode can be downloaded off the internet and executed on a user's machine (Applets), a malicious actor could:
- Request excessive system permissions
- Crash the host system
- Steal/copy user data

#### The Fix: Sandbox Model

- Recall: the **JVM** is the component that actually executes bytecode by translating it to native machine instructions.
- Java made the JVM run all bytecode inside a **restricted/sandboxed environment** — untrusted code cannot request or obtain permissions beyond what the sandbox allows.
- This is called Java's **Sandbox Model**: a restricted execution space that prevents code from accessing resources it shouldn't.

> **Key insight (interview-worthy):** Both of Java's two hardest problems — Portability AND Security — are solved by the **same single component: the JVM**. Simplicity, by contrast, is solved at the language/syntax level, not by the JVM.

#### Historical footnote
Applets were deprecated around **JDK 10/11** (per the lecture's approximation) once no one needed browser-embedded bytecode anymore (JavaScript took over the frontend entirely).



### 6. Could C/C++ Have Done the Same Thing?

**Yes — architecturally, nothing stops you from building an intermediate-bytecode + "C++ Virtual Machine" model.**

- This wasn't done for C/C++ deliberately: C/C++'s entire design goal was to **stay close to hardware and be maximally fast**, not to be portable/general-purpose. Adding a VM layer would contradict its purpose.
- **Microsoft actually did this** — with **C#**, an alternative to Java, explicitly close in syntax to C++ (hence the name), made platform-independent using the same bytecode+VM philosophy (CLR instead of JVM). It succeeded technically but never overtook Java's popularity.
- Later "modern" languages (C#, Python, etc.) carried forward the same platform-independence philosophy Java pioneered, each with its own implementation details (compiled vs interpreted execution differs — covered in a later lecture).



#### Quick Self-Check (answer before reading the explanation)

> **Q1.** You have identical `hello.cpp` source code. You compile it on a Windows/x86 machine and on a macOS/ARM machine. Will the two resulting binaries be identical? Why or why not?

*Answer:* No. Two independent causes: (1) OS-specific system library calls get compiled into the binary differently for Windows vs macOS, and (2) the two processors have different ISAs (grammars), so the same logical instruction gets encoded differently at the hardware level.

> **Q2.** Is the JVM itself platform-independent?

*Answer:* No — the JVM is platform-**dependent**; a separate JVM build exists per platform. Only the **bytecode** is platform-independent.

> **Q3.** Name the single JVM feature responsible for solving BOTH portability and security.

*Answer:* The JVM handles portability (bytecode → native code translation per platform) and security (sandboxed execution of that bytecode) — both live in the same component.



#### Golden Rules / Checklist

- [ ] **Platform = Processor + OS.** Never just say "computer" — be precise.
- [ ] **ISA = Instruction Set Architecture** = the "grammar" a specific processor understands (ADD, LOAD, STORE, JUMP...).
- [ ] C/C++ are platform-dependent because compiling produces different machine code per platform (OS lib differences + ISA differences).
- [ ] Java compiles source → **bytecode** (`.class`), an intermediate, platform-independent format — NOT machine code.
- [ ] The **JVM** (installed per-platform) converts bytecode → native machine code at runtime. JVM itself is platform-**dependent**.
- [ ] **WORA** = Write Once, Run Anywhere → compile once, JVM handles the rest per platform.
- [ ] Java's 3 founding pillars: **Portable, Simple, Secure.**
  - Portable → Bytecode + JVM
  - Simple → removed pointers, multiple inheritance, manual deallocation
  - Secure → JVM's **Sandbox Model** (restricted execution environment)
- [ ] Portability and Security are BOTH solved by the JVM — Simplicity is solved by language design/syntax.
- [ ] C# (Microsoft) proved the bytecode+VM trick is language-agnostic — it was applied to a C++-adjacent language deliberately, unlike C/C++ itself.
- [ ] Applets (frontend) and Servlets (backend) were Java's early full-stack use cases; Applets are now deprecated (~JDK 10/11).



#### Practice Questions

**Basic**
1. Define "platform" in the context of programming languages.
2. What is the difference between source code, bytecode, and machine code?
3. What does WORA stand for and what does it mean practically?

**Intermediate**
4. Explain, with an example, why the same C++ source file produces different binaries on Windows/x86 vs macOS/ARM.
5. What is an ISA, and why does it differ between Intel and ARM processors?
6. If bytecode is platform-independent, why does the JVM need to be platform-dependent?

**Advanced / Interview-style**
7. "Java solved portability by removing the need to compile per-platform." Critically evaluate this statement — is compilation truly eliminated, or just moved?
8. Explain how Java's Sandbox Model relates architecturally to its portability solution. Why do both rely on the JVM specifically?
9. Why didn't C/C++ adopt a bytecode+VM model themselves, and why did Microsoft succeed in doing this with a *new* language (C#) instead of retrofitting C++?
10. A candidate says "Java is slower than C++, so portability doesn't matter today." How would you respond, using the historical argument from this lecture about processor speed trends?


## 2. JVM, JRE, JDK & Java Editions (JSE/JEE/JME)

> Source: Coder Army Core Java series, Lecture 2
> Depth level: 2–3 YOE — internals of execution pipeline, not just definitions



### 1. Recap: The Pipeline So Far

```
hello.java --[compiler]--> bytecode (hello.class) --[JVM]--> machine code (platform-specific) --> CPU executes --> output
```

- Bytecode is platform-**independent**.
- Machine code is platform-**dependent** (different per platform, since platform = OS + processor/ISA, covered in Lecture 1).
- The JVM's job: convert bytecode → machine code for its specific platform.

This lecture goes one layer deeper: **how** exactly the JVM does that conversion, plus the three-tier hierarchy **JVM → JRE → JDK**.



### 2. The Three-Tier Hierarchy (Concentric Circles)

```
┌─────────────────────────────────────┐
│  JDK  (Java Development Kit)         │
│  ┌─────────────────────────────────┐ │
│  │  JRE (Java Runtime Environment) │ │
│  │  ┌───────────────────────────┐  │ │
│  │  │  JVM (Java Virtual Machine)│ │ │
│  │  └───────────────────────────┘  │ │
│  │        + Class Libraries        │ │
│  └─────────────────────────────────┘ │
│     + Compiler (javac) + Debugger    │
│           + javadoc, etc.            │
└─────────────────────────────────────┘
```

| Layer | Full Form | = | Purpose |
|-|-|-|-|
| **JVM** | Java Virtual Machine | — | Executes bytecode → converts to native machine code, provides security + GC |
| **JRE** | Java Runtime Environment | JVM + Class Libraries | Enough to **run** a compiled Java program |
| **JDK** | Java Development Kit | JRE + Compiler (`javac`) + Debugger + Docs tools | Enough to **write, compile, debug, and run** Java programs |

> **Golden rule:** Each layer is a strict superset of the one inside it. You cannot meaningfully install "just a JRE" for development today — you need the JDK, which contains everything.



### 3. JVM In-Depth: How Bytecode Actually Becomes Machine Code

#### Compiler vs Interpreter — the fundamental distinction

Both take source code (or bytecode) and produce machine code. The difference is **how much they read before producing output**.

|- | Compiler | Interpreter |
|-|-|-|
| Reads | Entire file at once | Line by line |
| Converts | All at once → full machine code, then runs | One line → machine code → runs → next line |
| Example (pure) | C, C++ (compiled languages) | Python (interpreted language, roughly) |

```
COMPILER:
source code ──(read entire file)──> full machine code ──> CPU runs it ──> output

INTERPRETER:
source code ──(read line 1)──> machine code (line 1) ──> CPU runs ──> output
           ──(read line 2)──> machine code (line 2) ──> CPU runs ──> output
           ──(read line 3)──> ... (repeats)
```

#### Is Java compiled or interpreted?

**Neither purely — Java is BOTH.**

1. **Stage 1 (compiled):** `javac` compiler converts `.java` source → `.class` bytecode (all at once).
2. **Stage 2 (interpreted, historically):** Inside the JVM, an **interpreter** converts bytecode → machine code **line by line**, and each converted line is immediately executed by the CPU.

```
hello.java --[javac, COMPILER]--> hello.class (bytecode)
hello.class --[JVM's INTERPRETER]--> machine code, line by line --> CPU executes each line --> output
```

#### Why did early Java (1990s) use an interpreter instead of a compiler inside the JVM?

Historical constraint, not a design preference:
- Goal: get the Java program running **as fast as possible** after bytecode is ready (since Java already pays a "double conversion" cost: source→bytecode, then bytecode→machine code — an extra step C/C++ never had).
- 1990s hardware limitations: **slow processors, limited RAM, slow disks.**
- A full compiler for the bytecode→machine-code step would have added noticeable startup latency on that hardware. An interpreter starts executing immediately, one line at a time — better perceived responsiveness under those constraints.

#### The Modern Fix: JIT Compiler (hybrid model)

As hardware improved, Java added a **JIT (Just-In-Time) Compiler** *alongside* the interpreter — **not as a replacement**.

**How the hybrid works:**
- The JVM profiles the bytecode as it runs and identifies **"hot" code** — parts executed very frequently (e.g., a loop body called thousands of times).
- **Hot/frequent code** → compiled directly to machine code by the **JIT compiler** (fast, optimized, done once, reused).
- **Cold/infrequent code** → still handled by the **interpreter**, line by line.

```
                     ┌── (hot / frequent code) ──> JIT Compiler ──> machine code (compiled once, reused)
Bytecode ──> JVM ────┤
                     └── (cold / rare code)   ──> Interpreter   ──> machine code (line-by-line, on demand)
```

> **Why not just use a compiler for everything, given modern fast hardware?** Compilation itself is still relatively slow compared to interpretation for a first pass. The hybrid gets the best of both: instant startup (interpreter) + long-run optimized performance for hot paths (JIT).

#### Why is Java still marginally slower than C/C++ in some benchmarks?

- C/C++: source → machine code, **one single conversion step**, direct execution.
- Java: source → bytecode → machine code, **two conversion steps** (compilation overhead is the price paid for portability).
- This gap has **shrunk drastically** since the 1990s as hardware and JIT technology improved — often negligible/imperceptible today.
- **Trade-off framing (important for interviews):** Portability is not free. Java's designers consciously traded a small amount of raw execution speed for platform independence, and engineered the interpreter+JIT hybrid specifically to minimize that cost over time.



### 4. Full List of JVM Responsibilities

Don't just say "JVM converts bytecode to machine code" in an interview — that's incomplete. The JVM does **three** things:

1. **Bytecode → Machine Code conversion**, via Interpreter + JIT Compiler (hybrid, covered above).
2. **Security**, via the **Sandbox Model** (from Lecture 1): bytecode never runs directly on the host system — it runs *inside* the JVM's controlled environment, which restricts unauthorized access (file deletion, malware injection, etc.), since bytecode can be downloaded from anywhere on the internet and cannot be blindly trusted.
3. **Garbage Collection**: automatic memory management happens inside the JVM. (Deep dive deferred to a later lecture — this removes the "manual deallocation" complexity that C/C++ had, per Lecture 1's "Java is Simple" pillar.)

> **Interview-gold summary line:** *"The JVM converts bytecode to machine code using a hybrid interpreter+JIT approach, provides a sandboxed security environment, and manages memory via garbage collection."*



### 5. JRE = JVM + Class Libraries

A bare JVM can convert bytecode to machine code — but it **cannot run a real program** on its own, because real programs call built-in functionality: printing to console, reading files, string manipulation, collections, etc.

- These built-in operations = **Class Libraries**.
- Analogous to `#include <iostream>` in C++, or `import` statements in Java itself — you're pulling in prewritten, precompiled functionality rather than writing everything from scratch.
- **JRE = JVM + Class Libraries** — the minimum needed to actually **execute** a Java program that uses any standard library call (which is virtually every real program).

> Historically you *could* install just a JRE to run (not develop) Java apps. Modern JDK distributions bundle everything, so a standalone JRE-only install is largely obsolete in practice today.



### 6. JDK = JRE + Development Tooling

**JDK (Java Development Kit) = JRE + Compiler (`javac`) + Debugger + other dev tools (e.g., `javadoc`).**

| Component | Role |
|-|-|
| `javac` (compiler) | Source (`.java`) → Bytecode (`.class`) |
| Debugger | Step through code line-by-line, inspect state at breakpoints |
| `javadoc` | Generates documentation from code comments |
| JRE (bundled) | Everything needed to actually run the resulting bytecode |

**You need the full JDK to develop** — writing code requires the compiler, which only exists in the JDK, not the bare JRE.



### 7. Java Editions: JSE vs JEE vs JME

These are **not versions of the language** — they are **different standard bundles/scopes** of what Java offers, targeted at different use cases.

| Edition | Full Form | Scope |
||||
| **JSE** | Java Standard Edition | **Core Java** — OOP, classes, methods, all the fundamentals. This entire series = JSE. |
| **JEE** (a.k.a. **Jakarta EE**) | Java Enterprise Edition | JSE + libraries/classes for **web apps** — servlets, transactions, etc. Needed before Spring Boot. |
| **JME** | Java Micro Edition | Lightweight edition for old feature-phone apps. **Obsolete** — replaced by Android (which can also use Kotlin, itself JVM-based). |

```
JSE (Core Java) ──> foundation for everything
   │
   ├──> JEE / Jakarta EE ──> web apps, servlets, eventually Spring Boot
   │
   └──> JME (deprecated) ──> replaced by Android development
```

> Note: JEE was renamed **Jakarta EE** — if you see either name, they refer to the same thing (post-transfer of Java EE governance from Oracle to the Eclipse Foundation).

**Learning path implied by the instructor:** Master JSE (Core Java) fully first → then JEE (web apps, Spring Boot foundations). JME can be skipped entirely — no modern relevance.



### 8. Writing & Running Your First Java Program (Behind the Scenes)

#### The two commands

```bash
# Step 1: Compile source -> bytecode (uses javac from the JDK)
javac Demo.java
# Produces Demo.class (bytecode) in the same directory

# Step 2: Run bytecode -> JVM converts to machine code -> CPU executes -> output
java Demo
# Note: no ".class" extension here — just the class name
```

#### What happens internally on `java Demo`

```
Demo.class (bytecode)
        │
        ▼
   JRE invoked
        │
        ▼
      JVM
   ┌────┴─────┐
   │          │
Interpreter  JIT Compiler   (hybrid, as discussed in §3)
   │          │
   └────┬─────┘
        ▼
  Machine code (platform-specific)
        │
        ▼
   CPU executes
        │
        ▼
     Output (e.g., "Hello World" on console)
```

#### First program (conceptual — syntax deep-dive comes in later lectures)

```java
// File: Demo.java
public class Demo {
    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}
```

- Not yet explained (deliberately deferred): `class`, `static`, `public void main`, `String[] args`, `System.out.println`. These are syntax topics for the next lectures.
- Key takeaway for **this** lecture: understanding *what happens between typing this code and seeing "Hello World" on screen* — not the syntax itself.

#### A practical note on `.class` files

- Opening a `.class` file directly shows **garbled/unreadable content** — it's bytecode, not English and not raw machine code either; it's a genuine intermediate format.
- Some IDEs (e.g., VS Code) will auto-**decompile** a `.class` file back into pseudo-source using a decompiler (e.g., "Fernflower") purely for human convenience — this is a display trick, not what's actually stored in the file.



#### Quick Self-Check

> **Q1.** You only install a bare JVM on a machine with no class libraries. Can you run a Java program that calls `System.out.println(...)`? Why or why not?

*Answer:* No. `System.out.println` is part of the **class libraries**, which live in the JRE (JVM + class libraries), not in the bare JVM. Without class libraries, the JVM has no implementation to call for that operation.

> **Q2.** Why does modern Java use both an interpreter AND a JIT compiler instead of just one or the other?

*Answer:* Pure interpretation is slow for repeatedly executed ("hot") code since it re-converts the same lines every time. Pure compilation upfront adds startup latency. The hybrid model interprets rarely-used code on the fly (fast startup) and JIT-compiles frequently-used code once into optimized machine code (fast steady-state performance).

> **Q3.** What's the minimum you need installed to just *run* someone else's compiled `.class` file (not develop your own program)?

*Answer:* A JRE (JVM + class libraries) is conceptually the minimum, though in practice today you install the full JDK regardless since standalone JRE distributions are largely obsolete.



#### Golden Rules / Checklist

- [ ] **JVM ⊂ JRE ⊂ JDK** — each one is the previous plus more.
- [ ] **JVM** = bytecode→machine code conversion (interpreter + JIT) + Sandbox security + Garbage Collection.
- [ ] **JRE** = JVM + Class Libraries → enough to **run** a Java program.
- [ ] **JDK** = JRE + Compiler (`javac`) + Debugger + doc tools → enough to **develop** a Java program.
- [ ] **Compiler**: reads entire source at once, converts all at once.
- [ ] **Interpreter**: reads/converts/executes line by line.
- [ ] **Java is BOTH compiled (source→bytecode) AND interpreted (bytecode→machine code, historically)** — never call it purely one or the other.
- [ ] **JIT Compiler** supplements (does not replace) the interpreter — it compiles only "hot"/frequently-executed bytecode for speed; cold code still goes through the interpreter.
- [ ] Java is marginally slower than C/C++ due to the extra compilation stage — but this is the deliberate cost of portability, and the gap is now negligible on modern hardware.
- [ ] **JSE** = Core Java (this whole series). **JEE/Jakarta EE** = JSE + web/enterprise libraries (needed before Spring Boot). **JME** = obsolete, replaced by Android.
- [ ] Compile: `javac FileName.java` → produces `FileName.class`. Run: `java FileName` (no extension).



#### Practice Questions

**Basic**
1. Write the exact terminal commands to compile and then run a file named `App.java`.
2. What does JRE stand for, and what two components make it up?
3. True or False: The JDK contains the JRE within it.

**Intermediate**
4. Explain the difference between a compiler and an interpreter using the "read all at once" vs "read line by line" framing.
5. List the three core responsibilities of the JVM (not just bytecode conversion).
6. Why can't you run a Java program using only a JVM, with no JRE?

**Advanced / Interview-style**
7. "Java is an interpreted language." Is this a fully accurate statement? Justify your answer using the compiled+interpreted hybrid model.
8. Explain how the JIT compiler decides what to compile vs. what the interpreter continues to handle. Why doesn't the JVM just JIT-compile everything on first execution?
9. In the 1990s, why did Java's designers choose an interpreter-first approach for the bytecode execution stage rather than a compiler, given that a compiler alone would technically produce faster steady-state code?
10. A colleague says "just install the JRE, we don't need the full JDK for local development." Explain why this is impractical for actually writing and compiling Java code, tying it back to what's absent in a bare JRE.
11. Compare JSE, JEE (Jakarta EE), and JME — what problem was each created to solve, and why is JME now considered obsolete?



## 3. Variables, Data Types, Literals & Keywords


### 1. Variables — What They Actually Are

**Definition:** A variable is a **named container that holds a value in memory (RAM)**.

- A program exists to *do* something (print output, perform calculations, build an app). The moment it needs to hold onto a piece of data across statements, that data must live in RAM, at some memory location.
- A **variable** = a human-friendly name (identifier) bound to that memory location, so you don't have to think in raw addresses.

```
Memory (conceptual, before we study heap/stack internals):

┌────────────┐   ┌────────────┐
│   x  → 2   │   │   y  → 3   │
└────────────┘   └────────────┘
```

> Everything is ultimately stored as binary — `2` is really stored as its bit pattern, not as the glyph "2". We're abstracting that away until the binary section below.

#### Naming matters — descriptive identifiers

Never name variables `x`, `y` in real code — prefer `firstNumber`, `secondNumber`. A good variable name should let you infer its purpose without reading surrounding logic. This is universal across languages, not Java-specific.



### 2. Identifiers

**Identifier** = the name you give to a variable (or, later, a method/class) so you can refer to the value it holds.

```
int firstNumber = 4;
    └────┬────┘
     identifier (refers to the container holding 4)
```

> Variables, identifiers, and keywords (below) are universal CS vocabulary — learn them once in Java, reuse the mental model in every other language.



### 3. The "Black Box" Learning Model (meta-note)

When reading real Java code for the first time, you will see boilerplate you don't yet understand (`public static void main(String[] args)`, `class`, etc.). **Treat it as a black box** — you know input/output behavior without needing full internals yet. Learning a language is never perfectly linear; some syntax must be provisionally accepted before its full explanation arrives later in the course. Don't get stuck trying to reverse-engineer every keyword on day one.



### 4. Variable Declaration Syntax

```java
dataType identifier = value;
```

Example:
```java
int firstNumber = 10;
```

- **Every statement ends with a semicolon (`;`)** — same convention as C/C++.
- The **data type must be specified up front** (before compilation even proceeds) — this is what makes Java a **statically typed** language.

> **Statically typed** = the type of every variable must be known at compile time, before the program runs. Contrast with **dynamically typed** languages (e.g., Python), where a variable's type is resolved at runtime. *Why* this matters becomes clearer once you understand how memory is allocated per type — covered below.



### 5. Primitive Data Types — Overview

Java has two broad categories of data types:

```
Data Types
├── Primitive       (this lecture)
└── Non-Primitive   (deferred — covered under OOP later)
```

**Primitive types** split into 4 families:

| Family | Types | Count |
|-|-|-|
| Integer | `byte`, `short`, `int`, `long` | 4 |
| Floating-point (real numbers) | `float`, `double` | 2 |
| Character | `char` | 1 |
| Boolean | `boolean` | 1 |



### 6. Integer Types: `byte`, `short`, `int`, `long`

All four represent whole numbers; they differ only in **size (bits) → range**.

| Type | Size | Range |
|-|-|-|
| `byte` | 8 bits | −128 to 127 |
| `short` | 16 bits | −32,768 to 32,767 |
| `int` | 32 bits | ≈ −2.1 billion to 2.1 billion |
| `long` | 64 bits | ≈ −9.2 quintillion to 9.2 quintillion |

```java
byte  b = 5;
short s = 10;
int   i = 4000;
long  l = 10000L;   // 'L' suffix recommended for long literals (see note below)
```

#### How Binary Representation Determines Range

Computers only understand `0` (off / no current) and `1` (on / current) — this is why everything reduces to binary.

**Positional values (like decimal's 1s/10s/100s places, but base-2):**

```
Bit position:   2³   2²   2¹   2⁰
                (8)  (4)  (2)  (1)
Example (4):     0    1    0    0   → 0100 = 4
Example (5):     0    1    0    1   → 0101 = 5
```

**Core formula — with `n` bits:**
- Total unique combinations = `2ⁿ`
- Largest *unsigned* value representable = `2ⁿ − 1`

Worked examples:
| Bits (n) | 2ⁿ (combinations) | Max unsigned value (2ⁿ−1) |
|-|-|-|
| 2 | 4 | 3 |
| 3 | 8 | 7 |
| 8 | 256 | 255 |

#### Why `byte`'s range is −128 to 127, not 0 to 255

> **Every number in Java is signed** — meaning it can represent both positive and negative values (there is no separate "unsigned byte" type in Java, unlike C).

Because the 8-bit space must be split between negative and positive halves:
```
[-128 ................. 0 ................. 127]
      (half for negatives)  (half for positives, minus zero itself)
```
So instead of `0–255` (unsigned), you get `−128 to 127` (signed) — same 256 total combinations, just centered around zero.

> **Deferred to next lecture:** *how* negative numbers are actually encoded in binary (two's complement) — the instructor explicitly parks this. **Interview note for now:** know that Java uses **two's complement** representation for negative integers; the mechanism (invert bits + add 1) is coming in the next lecture's notes.

#### `long` literal suffix — `L`

```java
long l = 10000L;   // WRONG (implicit, works for small values by luck): long l = 10000;
                    // RIGHT (explicit, avoids overflow bugs on large literals): long l = 10000L;
```
Without the `L` suffix, a numeric literal is treated as `int` by default. For values that fit in `int` range this coincidentally still works, but it's a latent bug source for genuinely large long literals — always suffix `L` for `long` literals as a habit.



### 7. Floating-Point Types: `float`, `double`

Represent real (decimal) numbers — e.g., `5.23`, `10.02`.

| Type | Size | Precision | Range (approx, scientific notation) |
|-|-|-|-|
| `float` | 32 bits | **Single precision** | ~4.9 × 10⁻³²⁴ to a large positive bound |
| `double` | 64 bits | **Double precision** | much wider than `float` |

```java
float  f = 10.54f;   // WRONG:  float f = 10.54;   // compile error: "incompatible types: possible lossy conversion from double to float"
                      // RIGHT:  float f = 10.54f;  // 'f' suffix tells compiler this literal is a float, not a double
double d = 23.0987;  // no suffix needed — decimal literals default to double
```

#### Why the `f` suffix is mandatory

By default, **any decimal literal is treated as `double`** by the compiler. Assigning a `double`-typed literal into a `float` variable is a narrowing conversion Java won't do implicitly (data/precision could be silently lost) — hence the compile error unless you explicitly mark the literal as `float` with `f`.

#### Golden rule: prefer `double` over `float` in real code

Two independent reasons:
1. **Precision** — `float`'s single precision is often insufficient for anything beyond loosely-precise use cases (e.g., dollars/cents where the fractional digits barely matter). Scientific/financial-grade calculations need `double`.
2. **Hardware optimization** — modern processors are optimized for `double` arithmetic; `float` optimization was more relevant to much older hardware. Every value representable in `float` is also representable in `double` (superset range), so there's rarely a reason to reach for `float`.
3. All of Java's built-in math methods (`Math.sin`, `Math.cos`, etc.) **return `double`**, reinforcing this as the ecosystem default.

> **Interview line:** *"`float` is largely legacy in modern Java code — `double` is preferred both for precision and because contemporary hardware/JIT paths are optimized for it."*

#### Scientific notation literals

```java
double d = 6.022e23;   // 6.022 × 10²³ (Avogadro's number) — 'e' = exponent marker
```



### 8. Character Type: `char`

Represents a **single Unicode character**, 16 bits wide.

```java
char c = 'a';   // WRONG: char c = a;    // compiler thinks 'a' (no quotes) is an identifier, not a literal
                 // RIGHT: char c = 'a';  // single quotes mark this as a character literal
```

#### Why single quotes are mandatory

Without quotes, the compiler can't distinguish a character literal from another identifier name. Single quotes disambiguate: *"what follows is a literal character, not a variable reference."*

#### How a `char` is actually stored — ASCII → Unicode

- Computers store everything as binary integers — a character is no exception. Internally, `char` is stored as an **integer code point**, then rendered back to a glyph based on the variable's declared type (`char`) at read time.
- **Pre-Java standard: ASCII** (American Standard Code for Information Interchange) — 8 bits, range 0–127, covers English letters (`a`-`z`, `A`-`Z`), digits, and common symbols. Example: `'A'` → `65`.
- **Java's innovation: Unicode.** Because Java aimed to be maximally portable and globally usable, ASCII's English-only, 8-bit scope wasn't enough. Java:
  - Widened `char` to **16 bits** (vs ASCII's 8), and
  - Adopted the **Unicode** standard, which assigns an integer code point to characters across **every world language/script** (Hindi, Tamil, Chinese, Greek, etc.), not just the English alphabet.

```
'a'  ──(compiler encodes as)──>  97 (its Unicode/ASCII code point)  ──(stored as binary)──>  01100001
```

- On retrieval, the compiler checks the variable's declared type: if it's `char`, it converts the stored integer *back* into the corresponding glyph using the Unicode table; if the variable had instead been typed `int`, the same underlying bits would just print as the raw number.

> **Interview-gold detail:** *"Java's `char` is 16-bit (not 8-bit like C's `char`) specifically because it uses Unicode instead of ASCII, to support internationalization out of the box."*



### 9. Boolean Type: `boolean`

Simplest primitive — exactly two possible values: `true` or `false`.

```java
boolean b = false;   // WRONG: boolean b = 0;      // does NOT compile in Java
                      // RIGHT: boolean b = false;  // boolean literals are strictly true/false
```

#### Key difference from C/C++

In C/C++, any non-zero integer is treated as truthy and `0` as falsy (implicit int↔bool conversion). **Java does not allow this.** `boolean` is a completely distinct type — `0`/`1` cannot substitute for `false`/`true`.



### 10. Literals

**Literal** = the actual raw value written in code (as opposed to the identifier/name pointing at it).

```java
int b = 12;
//      └┬┘
//    literal (the value itself, "12")
//  └┬┘
// identifier (the name "b")
```

- Identifier → *how* you refer to the value.
- Literal → the value *itself*, as written.



### 11. Comments

```java
// Single-line comment — compiler ignores everything after // on this line

/*
   Multi-line comment.
   Compiler ignores everything between /* and *​/.
*/
```

Comments are purely for human readers — the compiler strips them before compilation. Universal across virtually all C-family languages.



### 12. Alternative Numeral Systems for Integer Literals

Java lets you write **any integer literal** (for `byte`, `short`, `int`, `long`) in four bases:

| Base | Prefix | Digits Allowed | Example (value 5) |
|-|-|-|-|
| Decimal | *(none)* | 0–9 | `5` |
| Binary | `0b` | 0–1 | `0b101` |
| Octal | `0` (leading zero) | 0–7 | `05` |
| Hexadecimal | `0x` | 0–9, a–f (a=10 … f=15) | `0x5` |

```java
byte b1 = 0b101;   // binary for 5
byte b2 = 05;      // octal for 5 (leading zero)
byte b3 = 0x5;      // hex for 5
byte b4 = 5;        // decimal — the default/idiomatic form
```

#### Common mistake — invalid digit for the base

```java
byte bad1 = 08;     // WRONG: octal only allows digits 0-7 — compile error "out of range"
byte bad2 = 0b2;     // WRONG: binary only allows 0 and 1 — '2' is not a valid binary digit
```

#### Hexadecimal digit mapping (base 16)

```
0 1 2 3 4 5 6 7 8 9 a  b  c  d  e  f
                    10 11 12 13 14 15
```

```java
int fifteen = 0xf;   // 15
int ten     = 0xa;   // 10
```

> **Practical note:** decimal is the overwhelming default in real code. Binary/octal/hex literals mainly matter for bit-manipulation-heavy or low-level code (flags, masks, protocol work) — good to know they exist and how to read them, rarely something you'll write day-to-day.

#### Readability: underscores in numeric literals

```java
long big = 123_456_789L;   // underscores are purely visual — compiler strips them entirely
```

**Rules:**
- Cannot place `_` immediately before/after a decimal point.
- Cannot place `_` immediately before/after the exponent marker `e`/`E`.
- Otherwise, place them anywhere useful (e.g., Indian digit grouping: `12_34_56_000`).



### 13. Declaration vs. Definition

These are commonly used interchangeably but are technically distinct:

```java
int x;      // DECLARATION — tells the compiler "reserve a 32-bit slot named x, of type int"
            // No value assigned yet.

x = 4;      // DEFINITION — now x actually holds a value (4)
```

versus the combined form used throughout this lecture:
```java
int x = 4;  // declaration + definition in a single statement
```

#### Why this distinction matters in practice

Most values in real programs are **not hardcoded** — e.g., a calculator app's two operands typically come from **user input**, not literals baked into the source. In that case, you *declare* the variable first (reserving its memory/type), then *define* (assign) it later once the value becomes available (e.g., after reading user input).

```
Declaration:  int firstNumber;              // reserve memory, type = int (32 bits)
   ...(later, e.g. after reading user input)...
Definition:   firstNumber = userInputValue; // now the reserved slot actually holds a value
```



### 14. Keywords

**Keywords = words reserved by the Java language itself**, which you **cannot** use as identifiers (variable names, and later, method/class names) because doing so would create ambiguity for the compiler.

```java
// WRONG: naming a variable after a reserved keyword
int class = 5;    // compile error — 'class' is a reserved keyword

// RIGHT: pick a non-reserved, descriptive name
int classCount = 5;
```

- Java has **68 keywords** total (e.g., `class`, `public`, `static`, `void`, `int`, `if`, `else`, ...).
- **Interesting historical trivia:** Java reserved a few words it has *never actually used* as functioning keywords — e.g., `goto` and `const`. These were reserved specifically because they existed as functional keywords in C/C++ (Java's spiritual predecessor), presumably in case Java adopted similar constructs — but the language evolved without needing them, and they remain reserved (unusable as identifiers) purely as a historical artifact.



#### Quick Self-Check

> **Q1.** Why does `float f = 10.54;` fail to compile, while `double d = 10.54;` compiles fine?

*Answer:* Any bare decimal literal defaults to `double`. Assigning a `double` literal to a `float` variable is a narrowing conversion the compiler won't do implicitly (possible precision loss) — you must explicitly suffix the literal with `f` (`10.54f`) to tell the compiler you intend a `float`.

> **Q2.** Why is `byte`'s range −128 to 127 instead of 0 to 255, even though it has 256 possible bit combinations?

*Answer:* Because every numeric type in Java is signed — half the bit-pattern space is used to represent negative numbers, so the 256 total combinations split into 128 negative values, zero, and 127 positive values.

> **Q3.** Why is Java's `char` 16 bits instead of the 8-bit `char` found in C?

*Answer:* Java uses the Unicode standard (to support every world language/script) rather than the 8-bit, English-only ASCII standard C used — Unicode requires more bits per character, hence 16-bit `char`.

> **Q4.** What's the difference between declaring and defining a variable?

*Answer:* Declaration reserves a named memory slot of a given type (`int x;`) with no value yet; definition assigns an actual value into that already-declared slot (`x = 4;`). `int x = 4;` does both in one statement.



#### Golden Rules / Checklist

- [ ] **Variable** = named container in memory. **Identifier** = its name. **Literal** = the raw value itself.
- [ ] Java is **statically typed** — every variable's data type must be declared up front, before compilation proceeds.
- [ ] Primitive types = **Integer** (`byte`, `short`, `int`, `long`), **Floating-point** (`float`, `double`), **Character** (`char`), **Boolean** (`boolean`). Non-primitive types deferred to OOP.
- [ ] Every numeric type in Java is **signed** — this is why ranges are split around zero (e.g., `byte`: −128 to 127, not 0–255).
- [ ] With `n` bits: `2ⁿ` unique combinations; max **unsigned** value = `2ⁿ − 1`.
- [ ] Negative number encoding (**two's complement**) is deferred — know the term exists for now.
- [ ] `float` needs an `f` suffix on its literal (`10.54f`); bare decimal literals default to `double`.
- [ ] **Prefer `double` over `float`** in real-world code — better precision, and modern hardware/JIT is optimized for it.
- [ ] `long` literals should carry an explicit `L` suffix as good practice, even though small values compile without it.
- [ ] `char` literals require **single quotes** (`'a'`); internally stored as a 16-bit Unicode code point, not an 8-bit ASCII code like C.
- [ ] `boolean` only accepts `true`/`false` — unlike C/C++, `0`/`1` are NOT valid substitutes.
- [ ] Integer literals can be written in **decimal** (default), **binary** (`0b`), **octal** (leading `0`), or **hexadecimal** (`0x`) — invalid digits for a base (e.g., `08` in octal) cause a compile error.
- [ ] Underscores (`_`) can be inserted in numeric literals for readability — never adjacent to a decimal point or exponent marker.
- [ ] **Declaration** (`int x;`) reserves memory + type; **Definition** (`x = 4;`) assigns a value. Combined form: `int x = 4;`.
- [ ] **Keywords** are reserved words (68 total in Java) that cannot be used as identifiers — e.g., `class`, `public`, `static`; some (`goto`, `const`) are reserved but never actually used, inherited from C/C++ heritage.
- [ ] Comments: `//` for single-line, `/* ... */` for multi-line — compiler ignores both entirely.



#### Practice Questions

**Basic**
1. Write a declaration+definition statement for an `int` variable named `age` holding `25`.
2. What is the difference between an identifier and a literal? Give an example of each from the same line of code.
3. Which of Java's primitive types is 16 bits wide and used to represent a single character?

**Intermediate**
4. Why does the following fail to compile, and how would you fix it?
   ```java
   float price = 19.99;
   ```
5. Convert the decimal number `13` to binary, octal, and hexadecimal literal form as you would write each in Java source code.
6. Explain, using the bit-position table, why `byte` can represent at most 256 distinct values but its range is −128 to 127, not 0 to 255.
7. What's wrong with this line, and why?
   ```java
   boolean isActive = 1;
   ```

**Advanced / Interview-style**
8. A teammate says "Java's `char` is basically the same as C's `char`, just wider." Correct this statement — what standard does each rely on, and why did Java need the wider type?
9. Explain why Java enforces static typing at compile time. What class of bugs does this catch that a dynamically typed language might not catch until runtime?
10. Why does Java prefer `double` over `float` for virtually all real-world numeric work today, when historically `float` existed as the "lighter" option? Name at least two distinct reasons.
11. Explain the practical difference between declaration and definition using a concrete scenario (e.g., a calculator app reading two numbers from user input) — why can't you always combine them into one statement?
12. Java reserves the keywords `goto` and `const` but never uses them as functioning language features. Why do you think a language designer would reserve a word it doesn't intend to implement?



## 4. How Negative Numbers & Floating-Point Numbers Are Stored in Memory




### 0. Why This Matters

Positive integers are trivial: convert to binary, store as-is. This lecture covers the two genuinely tricky cases:
1. **Negative integers** → solved with **two's complement**.
2. **Floating-point numbers** → solved with the **IEEE-754** standard (sign + exponent + mantissa), and this is where **precision loss** becomes visible and explainable.

Both are classic interview topics because they reveal whether you understand what's happening *underneath* the language.



### 1. How Negative Numbers Are Stored — Two's Complement

#### Step-by-step recipe

Given a negative number, e.g. `byte b = -42;` (byte = 8 bits, range −128 to 127):

```
STEP 1 — Take the binary of the magnitude (ignore the sign):
   42 (decimal) → 00101010  (8-bit binary)

STEP 2 — First complement (a.k.a. one's complement): flip every bit (0→1, 1→0)
   00101010 → 11010101

STEP 3 — Second complement (a.k.a. two's complement): add 1 to the first complement
   11010101 + 1 = 11010110

RESULT: -42 is stored in memory as 11010110
```

```java
byte b = -42;   // internally stored as the two's-complement bit pattern 11010110
```

#### Converting Decimal → Binary (quick method, division by 2)

```
42 / 2 = 21  remainder 0   ┐
21 / 2 = 10  remainder 1   │  read remainders
10 / 2 = 5   remainder 0   │  bottom → top
5  / 2 = 2   remainder 1   │  to get the binary
2  / 2 = 1   remainder 0   │
1  / 2 = 0   remainder 1   ┘
Result (bottom-to-top): 101010  → pad to 8 bits: 00101010
```

#### MSB and LSB

- **MSB (Most Significant Bit)** = the **left-most** bit.
- **LSB (Least Significant Bit)** = the **right-most** bit.
- "Significance" = how much the overall value changes if that bit flips. Flipping the LSB changes the value by 1; flipping the MSB changes it by a huge amount (e.g., in decimal, changing the last digit of `2321` barely moves the value, changing the first digit swings it by thousands).

#### The MSB is the sign indicator

```
MSB = 0  →  number is POSITIVE
MSB = 1  →  number is NEGATIVE
```

**Why this never causes ambiguity with legitimate positive numbers:**

For an 8-bit `byte`, the largest positive value is `127`. Representing `127` requires bits 0–6 all set to `1`, but bit 7 (the MSB, worth `128`) **must stay `0`** — because if it were `1`, you'd be representing `128` or higher, which is outside `byte`'s positive range entirely. So:

```
Max positive byte (127):  0 1111111   ← MSB is always 0 for any valid positive value
```

Therefore, **the MSB can only ever be `1` as a result of the two's-complement encoding of a negative number** — it's never ambiguous with a legitimately large positive value, because that value wouldn't fit in the type's range in the first place.

#### Retrieving (decoding) a negative number from memory

Given the stored bit pattern, the CPU:
1. Checks the MSB. If `1` → the number is negative.
2. Computes the **first complement** (flip all bits) of the stored pattern.
3. Adds `1` to get the **second complement**.
4. Converts that result to decimal, and **prepends a negative sign**.

```
Stored: 11010110
Step 1 (flip):        00101001
Step 2 (add 1):       00101010  → decimal 42
Step 3 (MSB was 1):   → result = -42
```



### 2. Why Two's Complement (not just One's Complement)? — The Zero Edge Case

If we stopped at **first complement** (simple bit inversion) to represent negatives, we'd get a broken edge case: **negative zero**.

```
byte b = 0;     → 00000000                      (fine — the only representation of zero)
byte b = -0;    → first complement of 00000000  → 11111111   ← this would be a DISTINCT
                                                                "negative zero" bit pattern!
```

Mathematically, `-0` and `0` must be the same value — having two different bit patterns for "zero" is a broken/invalid representation.

#### How two's complement fixes this

Take the first complement of zero (`11111111`), then add 1 for the second complement:

```
  11111111
+        1
--
 100000000   ← 9 bits! But byte only stores 8 bits.
```

The 9th bit (the overflow carry) is simply **discarded** (out of the byte's storage width), leaving:

```
00000000   ← back to plain zero!
```

**Because of this overflow-and-discard behavior, `-0` collapses back to the same bit pattern as `+0`.** There is exactly one representation of zero — the ambiguity is eliminated. This is precisely *why* Java (and virtually every modern system) uses two's complement rather than one's complement for signed integers.

> **Interview-gold line:** *"Two's complement is preferred over one's complement specifically because it produces a single, unambiguous representation of zero — one's complement would otherwise create both a +0 and a distinct −0 bit pattern."*



#### Quick Self-Check — Two's Complement

> **Q1.** Why can't the MSB of a valid positive `byte` value ever be `1`?

*Answer:* Because a `byte`'s positive range tops out at 127, which requires bits 0–6 set and the MSB (worth 128) left at 0. Any pattern with the MSB set to 1 would represent a value ≥128, which is outside the positive range of `byte` — so MSB=1 is reserved exclusively for negative (two's-complement) values.

> **Q2.** What would go wrong if Java used one's complement instead of two's complement for negative numbers?

*Answer:* One's complement produces two distinct bit patterns for zero (`00000000` for +0, `11111111` for −0), which is mathematically invalid — zero should have exactly one representation.



### 3. How Floating-Point Numbers Are Stored — IEEE-754

#### Why this is a different problem entirely

Floating-point storage isn't about sign — it's about representing a number with a fractional (decimal) component using only bits, following an internationally standardized layout: the **IEEE-754 standard**.

#### Bit layout

| Type | Total bits | Sign | Exponent | Mantissa (fraction) |
|-|-|-|-|-|
| `float` | 32 | 1 bit | 8 bits | 23 bits |
| `double` | 64 | 1 bit | 11 bits | 52 bits |

```
float (32 bits):
┌─┬────────┬───────────────────────┐
│S│Exponent│       Mantissa        │
│1│   8    │          23           │
└─┴────────┴───────────────────────┘

double (64 bits):
┌─┬────────────┬─────────────────────────────────────────────────┐
│S│  Exponent  │                    Mantissa                     │
│1│    11      │                     52                          │
└─┴────────────┴─────────────────────────────────────────────────┘
```

#### Storage algorithm (encoding a float, e.g. `8.125f`)

**Step 1 — Convert to binary.**
- Integer part (left of decimal): standard division-by-2 method → `8 = 1000`
- Fractional part (right of decimal): **multiply by 2 repeatedly**, taking the integer part each time:
  ```
  0.125 * 2 = 0.25   → take 0
  0.25  * 2 = 0.5    → take 0
  0.5   * 2 = 1.0    → take 1   (stop — remainder is exactly 0)
  Fractional binary (read top→bottom): 001
  ```
- Combined: `8.125` → `1000.001`

**Step 2 — Normalize to `1.x × 2^exponent` form.**
```
1000.001 → move decimal point 3 places left → 1.000001 × 2³
```
(Base is 2, not 10, because we're in binary — moving the point `n` places left/right multiplies/divides by `2ⁿ`.)

**Step 3 — Add the bias to the exponent, then convert that biased exponent to binary.**
```
exponent = 3
bias (float) = 127
biased exponent = 3 + 127 = 130 → binary: 10000010  (8 bits)
```

**Step 4 — Place the pieces into the 32-bit layout.**
```
Sign (1 bit):        0            (positive number)
Exponent (8 bits):   10000010     (130, biased)
Mantissa (23 bits):  00000100000000000000000
                     └┬┘
                the "x" part of 1.x — note the leading "1." is implicit and NOT stored
```

#### Decoding (retrieval) formula

```
value = (-1)^sign × (1 + mantissa) × 2^(exponent − bias)
```

- `(-1)^sign`: sign=0 → multiply by 1 (unchanged); sign=1 → multiply by −1 (negated).
- `(1 + mantissa)`: the implicit leading `1.` is added back to the stored fractional mantissa bits.
- `2^(exponent − bias)`: subtracting the bias recovers the original (possibly negative) exponent.

Verifying `8.125f`:
```
(-1)^0 × (1 + 0.000001₂) × 2^(130−127)
= 1 × 1.015625 × 2³
= 1.015625 × 8
= 8.125   ✓ exact match
```



### 4. Why the Bias Exists (and why it's `127` for `float`)

**Purpose:** IEEE-754 wants to avoid storing a *signed* (possibly negative) exponent directly, because negative-exponent arithmetic complicates hardware comparison/calculation logic. Instead, it **shifts every exponent into an always-non-negative range** by adding a fixed bias — effectively converting a signed exponent into an unsigned one for storage purposes.

**Formula for the bias**, given `n` exponent bits:
```
bias = 2^(n-1) - 1
```

| Type | Exponent bits (n) | Bias |
|-|-|-|
| `float` | 8 | 2⁷ − 1 = **127** |
| `double` | 11 | 2¹⁰ − 1 = **1023** |

> The exact choice of `2^(n-1) - 1` (rather than, say, `2ⁿ - 1`) is itself an IEEE-754 hardware-optimization decision — you don't need to re-derive *why* that specific split was chosen, just know the formula and that it centers the representable exponent range symmetrically around zero.



### 5. Why `0.7f` Doesn't Print Back Exactly — Non-Terminating Binary Fractions

#### The observed behavior

```java
float f = 8.125f;
System.out.printf("%.20f", f);   // prints: 8.12500000000000000000  (exact!)

float f2 = 0.7f;
System.out.printf("%.20f", f2);  // prints: 0.69999998807907104492  (NOT exact!)
```

#### Why `8.125` is exact but `0.7` is not

`8.125`'s fractional part (`0.125`) terminates cleanly in binary (`0.001`) because `0.125 = 1/8 = 2⁻³` — a power of two, which binary represents exactly.

`0.7`, however, **never terminates in binary** — the multiply-by-2 process repeats forever:

```
0.7 × 2 = 1.4 → 1, remainder 0.4
0.4 × 2 = 0.8 → 0, remainder 0.8
0.8 × 2 = 1.6 → 1, remainder 0.6
0.6 × 2 = 1.2 → 1, remainder 0.2
0.2 × 2 = 0.4 → 0, remainder 0.4   ← repeats from here, infinitely
```

Result: `0.10110011001100110011...` (the pattern `0110` repeats forever) — an **infinitely repeating binary fraction**, exactly analogous to how `1/3 = 0.3333...` repeats forever in decimal.

#### The consequence

Since `float` only has **23 mantissa bits** (and `double` has 52), the infinitely repeating binary expansion must be **truncated** at that bit width. The stored value is therefore the *closest representable approximation* to `0.7`, not `0.7` exactly. When retrieved and converted back to decimal, you get something extremely close (`0.69999998...` or similar) but not bit-perfect.

> **Interview-gold summary:** *"Floating-point precision loss isn't a Java bug — it's inherent to IEEE-754 binary representation. Any decimal fraction that isn't a sum of exact negative powers of two (like 0.5, 0.25, 0.125) will have a non-terminating binary expansion, and gets silently truncated to fit the mantissa's bit width."*

#### Practical implication

```java
// WRONG (for money/precision-critical code): relying on float/double for exact decimal arithmetic
double price = 0.1 + 0.2;   // famously does NOT equal exactly 0.3 in virtually any language using IEEE-754

// RIGHT: use BigDecimal for exact decimal representation (covered in a later lecture, non-primitive types)
```

> `System.out.println` by default **rounds** the displayed value, which is why `0.7f` printed as `0.7` in the earlier, non-formatted lecture example — Java was quietly hiding the underlying imprecision from you. Using `%.20f` formatting (or the C-style `printf`) forces the full, un-rounded stored value to be revealed.



### 6. `double` — Same Mechanism, Wider Fields

```java
double d = 32.4156;
```

Identical algorithm to `float`, just with wider bit allocations:

| Field | `float` | `double` |
|-|-|-|
| Sign | 1 bit | 1 bit |
| Exponent | 8 bits | 11 bits |
| Mantissa | 23 bits | 52 bits |
| Bias | 127 | 1023 |

More mantissa bits = more precision before truncation kicks in — this is *why* `double` gets closer to the true value than `float` for the same fractional number, but **neither eliminates the fundamental non-terminating-binary-fraction problem.** Any repeating binary fraction (like `0.7`) will still lose precision in `double`, just at a much smaller/less noticeable magnitude.

#### Forward pointer: `BigDecimal`

Java provides a non-primitive type, **`BigDecimal`**, that stores decimal values **exactly** (no binary-fraction approximation) — at the cost of being slower and more memory-intensive than `float`/`double`. Deferred to a later lecture on non-primitive types. Know for now: *if you need guaranteed-exact decimal arithmetic (e.g., financial calculations), `BigDecimal` — not `double` — is the correct tool.*



#### Quick Self-Check — Floating Point

> **Q1.** Why does `8.125f` round-trip perfectly through storage and retrieval, but `0.7f` does not?

*Answer:* `8.125`'s fractional part (`0.125 = 2⁻³`) is an exact power of two, so its binary representation terminates cleanly. `0.7` has no exact finite binary representation — its binary expansion repeats infinitely, so it must be truncated to fit the mantissa's fixed bit width, producing a close-but-inexact approximation.

> **Q2.** What is the bias for `double`, and how is it derived?

*Answer:* `double` has 11 exponent bits, so bias = `2^(11-1) - 1 = 2^10 - 1 = 1023`.

> **Q3.** In the retrieval formula `(-1)^sign × (1 + mantissa) × 2^(exponent − bias)`, why is there an implicit `+1` added to the mantissa?

*Answer:* Because during storage, the value is normalized to the form `1.x × 2^exponent`, and only the fractional part (`x`) after the leading `1.` is actually stored in the mantissa bits — the leading `1` is implicit (always assumed) and must be manually re-added during decoding.



#### Golden Rules / Checklist

**Negative numbers (integers):**
- [ ] **MSB = 0 → positive; MSB = 1 → negative.** This is never ambiguous because a value with MSB=1 would exceed the type's positive range anyway.
- [ ] To encode a negative number: (1) binary of the magnitude, (2) flip all bits (**first/one's complement**), (3) add 1 (**second/two's complement**) → store this.
- [ ] To decode a stored negative bit pattern: (1) check MSB=1, (2) flip all bits, (3) add 1, (4) convert to decimal, (5) prepend a minus sign.
- [ ] **Two's complement (not one's complement)** is used specifically to avoid a duplicate "negative zero" bit pattern — the extra +1 step causes an overflow that's discarded, collapsing `-0` back to `0`.

**Floating point:**
- [ ] IEEE-754 layout: `float` = 1 sign + 8 exponent + 23 mantissa bits (32 total). `double` = 1 sign + 11 exponent + 52 mantissa bits (64 total).
- [ ] Encoding steps: binary conversion → normalize to `1.x × 2^exponent` → add bias to exponent → store sign/biased-exponent/mantissa (mantissa stores only the `x` part, leading `1.` is implicit).
- [ ] **Bias formula:** `2^(n−1) − 1` where `n` = number of exponent bits. `float` bias = 127, `double` bias = 1023. Purpose: keeps the stored exponent always non-negative for simpler hardware arithmetic.
- [ ] **Decoding formula:** `value = (-1)^sign × (1 + mantissa) × 2^(exponent − bias)`.
- [ ] Precision loss is **not a bug** — it's inherent to representing non-power-of-two fractions (like `0.7`, `0.1`) in binary, which never terminate and must be truncated to the mantissa's fixed width.
- [ ] `println` rounds displayed floating-point output by default, hiding the true stored imprecision — use precise formatting (`%.20f` / `printf`) to reveal the actual stored value.
- [ ] For exact decimal arithmetic (money, precision-critical domains), use **`BigDecimal`**, not `float`/`double`.



#### Practice Questions

**Basic**
1. What does MSB stand for, and what does its value (0 vs 1) tell you about a stored integer?
2. Name the two steps that convert one's complement into two's complement.
3. How many total bits, sign bits, exponent bits, and mantissa bits does a `double` use?

**Intermediate**
4. Manually compute the two's complement representation of `-5` as an 8-bit `byte`.
5. Why is `127` chosen as the bias for `float`'s 8-bit exponent field? Show the formula.
6. Explain, step by step, why storing `0.1` as a `float` will not round-trip to exactly `0.1` when printed with high decimal precision.
7. What would happen (in terms of bit patterns) if Java used one's complement instead of two's complement to store `0`, and then someone wrote `-0` in code?

**Advanced / Interview-style**
8. Walk through, from first principles, why the exponent field in IEEE-754 uses a "biased" representation instead of storing a signed exponent directly (e.g., via two's complement, the same scheme used for integers).
9. A junior developer is confused because `System.out.println(0.1f + 0.2f)` doesn't print exactly `0.3`. Explain why, referencing the mantissa truncation concept, and what data type/class they should use instead if exact decimal precision is required.
10. Derive, without looking it up, why a `byte`'s legitimate positive range can never produce an MSB of 1 — connect this directly to why MSB is a safe and unambiguous sign indicator.
11. Compare the precision differences between `float` and `double` when representing `0.7`. Will `double` ever perfectly represent `0.7`? Why or why not?


## 5. Type Conversion & Type Promotion


### 1. Two Categories of Type Conversion

```
Type Conversion
├── Implicit  — compiler does it automatically (no code needed from you)
└── Explicit  — you must tell the compiler what to do, via casting
```



### 2. Implicit Conversion — "Widening Conversion"

#### The rule

> **The destination data type must be WIDER (larger bit-width / bigger range) than the source data type.**

```java
byte b = 24;
int i = b;       // WRONG (if you think this needs a cast): int i = (int) b;
                  // RIGHT: no cast needed — this is valid Java as-is
System.out.println(i);   // prints 24
```

#### Why this works safely — always

```
byte  →  8 bits,  range −128 to 127
int   → 32 bits,  range ≈ −2.1 billion to 2.1 billion
```

Every value representable in `byte` is *guaranteed* to fit inside `int`'s much larger range — so there's no risk of data loss. The compiler can perform this conversion silently because **no information can possibly be lost** going from a narrower range into a wider one.

#### The widening hierarchy

```
byte → short → int → long → float → double
                char ↗
```

Any conversion moving strictly rightward/upward in this chain is implicit (widening). This applies uniformly:

```java
short s = 100;
int i2 = s;     // implicit — short (16-bit) fits into int (32-bit)

long l = i2;    // implicit — int (32-bit) fits into long (64-bit)
```

#### Special case: `char` → `int` (also implicit/widening)

```java
char c = 'a';
int i3 = c;     // implicit — no cast needed
System.out.println(i3);   // prints 97 (the Unicode code point of 'a')
```

**Why this works:** `char` is 16 bits, `int` is 32 bits — `char`'s entire representable range fits inside `int`. Internally, every `char` is *already* stored as an integer (its Unicode code point, per Lecture 3) — assigning it to an `int` variable just means the compiler stops treating those bits as "look up the corresponding glyph" and instead treats them as a plain number. This is why printing an `int` that received a `char`'s value shows the numeric code point (`97`), not the letter (`'a'`).



### 3. Explicit Conversion — "Narrowing Conversion"

#### The rule

> **When the destination is SMALLER than the source, Java refuses to convert automatically** — because data could genuinely be lost/truncated. You must explicitly acknowledge this risk using a **cast**.

```java
int i = 300;
byte b = i;             // WRONG: compile error — "incompatible types: possible lossy conversion from int to byte"
byte b = (byte) i;      // RIGHT: explicit cast — tells the compiler "I accept the possible data loss"
```

#### Cast syntax

```java
destinationType variable = (destinationType) sourceValue;
```

The `(byte)` in front of `i` is the cast operator — it's you, the programmer, explicitly telling the compiler: *"I know this might lose data. Do it anyway."*

#### What actually happens during a narrowing int→byte cast — the mechanics

```java
int i = 300;
byte b = (byte) i;
System.out.println(b);   // NOT 300 — prints 44
```

**Why 44, step by step:**

```
Step 1 — Binary of 300 (32-bit int):
   00000000 00000000 00000001 00101100

Step 2 — byte only has 8 bits of storage, so Java keeps ONLY the
         lowest (least-significant) 8 bits and discards the rest:
   ........ ........ ........ 00101100
                                └───┬───┘
                              kept (8 bits)

Step 3 — Convert the kept 8 bits to decimal:
   00101100 = 32 + 8 + 4 = 44
```

**Faster equivalent method — the modulo shortcut:**

```
result = sourceValue % (range of destination type)
300 % 256 = 44          // 256 = 2^8 = total combinations a byte can hold
```

> Both methods give identical results because taking the lowest N bits of a binary number is mathematically the same operation as computing `value mod 2ⁿ`.

```java
// WRONG: assuming narrowing "just works" without understanding truncation
byte result = (byte) 300;   // silently gives 44, NOT an error, NOT 300 — a real bug source if unnoticed

// RIGHT: know that narrowing conversions truncate — validate ranges before casting if correctness matters
if (i >= Byte.MIN_VALUE && i <= Byte.MAX_VALUE) {
    byte safe = (byte) i;
}
```

> **Interview-gold line:** *"A narrowing int-to-byte cast doesn't round or clamp the value — it discards all bits beyond the target type's width and reinterprets whatever remains, which is mathematically equivalent to `value % 2ⁿ`."*



### 4. Truncating Conversion — a Special Case of Narrowing

This specifically covers converting a **floating-point type → an integer type**, even when the two types are the *same bit-width* (e.g., `float` and `int` are both 32 bits).

```java
float f = 16.25f;
int i = f;          // WRONG: still a compile error — "incompatible types: possible lossy conversion from float to int"
int i = (int) f;    // RIGHT: explicit cast required
System.out.println(i);   // prints 16 — the fractional part is discarded entirely
```

#### Why this needs a cast even though both types are 32 bits

Bit-width alone doesn't guarantee compatibility — `int` **cannot represent a fractional/decimal component at all**, while `float` can. So even though no *range* overflow occurs here, there is guaranteed **information loss** (the decimal part), so Java still requires an explicit cast.

#### What actually happens: truncation, not rounding

```java
int i = (int) 16.25f;   // → 16 (fractional .25 is simply dropped, NOT rounded)
int j = (int) 16.99f;   // → 16 (NOT 17 — still truncates toward zero, doesn't round)
```

> **Common misconception to flag in interviews:** casting `float`/`double` to an integer type in Java **truncates** (drops the decimal part) — it does **not** round to the nearest integer. For actual rounding, you'd use `Math.round(...)` (a topic covered separately in the built-in methods section, not this lecture).



### 5. `boolean` — No Conversion Possible, In Either Direction

```java
boolean bool = false;
int i = bool;          // WRONG: compile error — "incompatible types: boolean cannot be converted to int"
int i = (int) bool;    // WRONG: still a compile error — "incompatible types: boolean cannot be converted to int" (casting doesn't help either)
```

#### Why this is fundamentally different from every other conversion above

Every other primitive type (`byte`, `short`, `int`, `long`, `float`, `double`, `char`) is, underneath, **some numeric encoding** — which is *why* conversions between them are even conceptually possible (they're all just reinterpreting/resizing bit patterns of numbers). `boolean` is not a numeric type at all in Java — `true`/`false` have no defined numeric equivalent (recall from Lecture 3: Java deliberately rejects the C/C++ convention of `0`/non-zero standing in for `false`/`true`). There is therefore no meaningful conversion path — not even via an explicit cast — between `boolean` and any other primitive type.

> **Golden rule:** `boolean` conversions to/from any other primitive type are **not possible** in Java, neither implicitly nor explicitly (casting doesn't unlock it — it's rejected at the language level, not just by default safety rules).



### 6. Summary Table — All Conversion Types

| Conversion type | Direction | Cast needed? | Data loss risk |
|||||
| **Widening (implicit)** | narrower → wider (`byte`→`int`, `char`→`int`, `int`→`long`, etc.) | No | None — always safe |
| **Narrowing (explicit)** | wider → narrower (`int`→`byte`, `long`→`int`, etc.) | Yes, `(type)` | Yes — value may be truncated to lowest N bits (`value % 2ⁿ`) |
| **Truncating (explicit)** | floating-point → integer (`float`→`int`, `double`→`long`, etc.) | Yes, `(type)` | Yes — decimal part is **discarded, not rounded** |
| **Boolean conversion** | `boolean` ↔ any numeric type | **Not possible at all**, cast or no cast | N/A — compile error regardless |



### 7. Automatic Type Promotion (in Expressions)

This is a **separate, related concept**: it governs what happens to operand types *during a calculation* — not during a plain assignment.

#### The triggering problem

```java
byte b = 50;
b = b * 2;    // WRONG (as written): compile error — "incompatible types: possible lossy conversion from int to byte"
```

**This is confusing at first glance** — no `int` appears anywhere in the code! But Java is silently promoting `b` to `int` *during the multiplication* before you ever see it.

#### The four rules of type promotion

```
Rule 1: byte, short, and char are ALWAYS promoted to int during any calculation.

Rule 2: If one operand in an expression is long,
        the ENTIRE expression's result becomes long.

Rule 3: If one operand in an expression is float,
        the ENTIRE expression's result becomes float.

Rule 4: If one operand in an expression is double,
        the ENTIRE expression's result becomes double.
```

**The underlying logic (single unifying idea):** *whichever type in the expression has the widest range "wins" — the whole expression's result is promoted to that widest type,* to guarantee the intermediate calculation never silently overflows or loses precision mid-computation.

#### Why the `b = b * 2` example fails

```
1. b (byte, 50) participates in a calculation → promoted to int (Rule 1)
2. b * 2 → int(50) * int(2) → result is int(100)
3. Assigning an int(100) result back into a byte variable → NARROWING → needs an explicit cast!
```

```java
// WRONG: byte = byte * literal, expecting it to "just work"
byte b = 50;
b = b * 2;              // compile error — RHS is promoted to int before multiplication

// RIGHT: explicitly cast the promoted int result back down to byte
byte b = 50;
b = (byte) (b * 2);     // parentheses matter — cast the whole expression's result, not just b
```

> **Common mistake:** writing `(byte) b * 2` casts only `b`, not the full expression result — always parenthesize the entire expression before casting it.

#### Worked example — chained promotion across a multi-type expression

```java
byte b = 50, a = 40, c = 100;
int i = a * b / c;
```

```
Step 1: a (byte) and b (byte) are both promoted to int (Rule 1) — happens automatically, even before multiplication
Step 2: a * b → int(40) * int(50) = int(2000)      [2000 exceeds byte's range, but that's fine — it's an int now]
Step 3: c (byte) is also promoted to int (Rule 1)
Step 4: 2000 / c → int(2000) / int(100) = int(20)
Step 5: int(20) assigned into int i → no cast needed (same type)
```

This is *exactly why* Rule 1 exists: without automatic promotion to `int`, the intermediate result `a * b = 2000` would overflow `byte`'s tiny −128..127 range mid-calculation, corrupting the final result before you ever got a chance to use a wider type.

#### Worked example — mixed float/int/double/short expression

```java
float f = 20.5f;
byte b2 = 12;
int i2 = 15;
char c2 = 'A';       // char code point 65
double d = 7.1234;
short s = 10;

float p1  = f * b2;   // float × byte → promoted per Rule 3 → result type: float
int   p2  = i2 / c2;  // int × char → char promoted to int (Rule 1) → result type: int
double p3 = d * s;    // double × short → promoted per Rule 4 → result type: double

double result = p1 + p2 + p3;
// p1 (float) + p2 (int) → float wins (Rule 3) → intermediate result: float
// (that float result) + p3 (double) → double wins (Rule 4) → final result: double
```

**Key insight:** promotion cascades left-to-right through a chained expression — each partial result re-triggers the same four rules against the next operand.



#### Quick Self-Check

> **Q1.** Why is `int i = someByteVariable;` always safe with no cast, but `byte b = someIntVariable;` requires an explicit cast?

*Answer:* `int` (32 bits) can represent every value `byte` (8 bits) can hold — no data loss is possible, so the compiler allows it implicitly. Going the other way, an `int` might hold a value outside `byte`'s range, so data could be lost — Java forces you to explicitly acknowledge that risk with a cast.

> **Q2.** What does `(int) 9.99` evaluate to, and why isn't it `10`?

*Answer:* `9`. Casting a floating-point value to an integer type **truncates** (discards the fractional part) rather than rounding — `Math.round()` would be needed to get `10`.

> **Q3.** Why does `byte b = 50; b = b * 2;` fail to compile even though the mathematical result (`100`) fits comfortably within `byte`'s range?

*Answer:* Because of automatic type promotion — `byte` operands are always promoted to `int` *before* any calculation happens (Rule 1), so `b * 2` produces an `int` result regardless of whether that specific result would have fit in a `byte`. Assigning that `int` result back into a `byte` variable is then a narrowing conversion requiring an explicit cast, even though no actual data would be lost in this particular case.

> **Q4.** Can you cast a `boolean` to an `int` in Java?

*Answer:* No — not implicitly, and not even with an explicit cast. `boolean` has no numeric representation in Java (unlike C/C++), so there is no valid conversion path to or from any numeric primitive type.



#### Golden Rules / Checklist

- [ ] **Implicit (widening) conversion:** destination type is wider than source → happens automatically, no cast, zero data loss risk. Chain: `byte → short → int → long → float → double`, with `char → int` also implicit.
- [ ] **Explicit (narrowing) conversion:** destination type is narrower than source → compiler refuses to do it silently, requires `(type)` cast, and **may truncate data** (equivalent to `value % 2ⁿ`, keeping only the lowest N bits).
- [ ] **Truncating conversion:** floating-point → integer, even at equal bit-width (e.g., `float`→`int`, both 32-bit) → still requires an explicit cast, and **discards the decimal part entirely** (truncates toward zero — does NOT round).
- [ ] `Math.round()` is needed for actual rounding — a plain cast never rounds.
- [ ] **`boolean` cannot be converted to/from any other primitive type**, implicitly or explicitly — there's no cast that makes this legal, because `boolean` has no numeric encoding in Java.
- [ ] **Type Promotion (4 rules)**, applied automatically during any *expression* (not plain assignment):
  1. `byte`, `short`, `char` → always promoted to `int` in any calculation.
  2. One operand `long` → entire expression becomes `long`.
  3. One operand `float` → entire expression becomes `float`.
  4. One operand `double` → entire expression becomes `double`.
- [ ] Type promotion is *why* `byte b = 50; b = b * 2;` fails to compile — `b` gets promoted to `int` before multiplication, and the `int` result then needs an explicit cast back to `byte`.
- [ ] Always cast the **entire expression result**, not just one operand: `(byte) (b * 2)`, never `(byte) b * 2`.



#### Practice Questions

**Basic**
1. Is `long l = someIntVariable;` implicit or explicit? Why?
2. What's the output of `System.out.println((int) 7.999);`?
3. Write the corrected version of this broken line: `byte result = someIntVariable;`

**Intermediate**
4. Explain, using the modulo shortcut, why `(byte) 260` evaluates to `4`.
5. A developer writes `short s = 10; s = s * 3;` and gets a compile error. Explain exactly why, referencing type promotion rules, and provide the fix.
6. Why does casting `float` to `int` require an explicit cast even though both are 32-bit types, unlike casting `int` to `long`, which is implicit despite being a bit-width increase?
7. What is the result type of the expression `charVar + intVar`, and why?

**Advanced / Interview-style**
8. Explain step by step what happens internally — at the bit level — when `int i = 1000; byte b = (byte) i;` executes. What value does `b` hold, and why?
9. Why can't Java allow even an explicit cast between `boolean` and `int`, when it allows explicit (if lossy) casts between every other primitive pair?
10. A candidate claims "type promotion and type casting are the same thing." Correct this — explain the distinct problem each one solves and give an example where you need both together in the same line of code.
11. Trace through the result type of this full expression step by step: `double result = floatVar * byteVar + intVar / charVar - longVar;` — name which promotion rule fires at each step.



## 6. Operators in Java



### 1. Arithmetic Operators

Standard: `+`, `-`, `*`, `/`, `%` (modulus — division's remainder).

```java
int a = 5, b = 10;
int c = a + b;   // 15
int d = a - b;   // -5
int e = a * b;   // 50
int f = b / a;   // 2
int g = b % a;   // 0
```

#### Compound assignment operators

```java
h += 2;   // shorthand for: h = h + 2
h -= 2;   // shorthand for: h = h - 2
h *= 3;   // shorthand for: h = h * 3
h /= 5;   // shorthand for: h = h / 5
h %= 5;   // shorthand for: h = h % 5
```

> **Why `=` is not mathematical equality:** in code, `=` never means "compare left and right" — it always means *"assign the right-hand value into the left-hand variable."* True equality comparison uses `==` (covered under Relational Operators below). This distinction is foundational and trips up anyone coming from a pure-math background.

#### Increment / Decrement

```java
i++;   // shorthand for: i = i + 1
i--;   // shorthand for: i = i - 1
```

> There is **no** `i**` (increment by "times") or equivalent for multiplication/division — only `+`/`-` get dedicated single-operand shortcuts, because incrementing/decrementing by exactly 1 is overwhelmingly the most common operation in loops and array traversal (multiplying by a fixed constant repeatedly is rarely useful).

#### Prefix vs. Postfix — the one place this actually matters

```java
int j = 9;
int k = j++;   // POSTFIX: assign j's CURRENT value to k FIRST, then increment j
               // equivalent to: k = j;  j = j + 1;
System.out.println(j + " " + k);   // 10 9   (j incremented, k got the OLD value)

int l = ++j;   // PREFIX: increment j FIRST, then assign the NEW value to l
               // equivalent to: j = j + 1;  l = j;
System.out.println(j + " " + l);   // 11 11  (j incremented, l got the NEW value)
```

```
POSTFIX (j++):                     PREFIX (++j):
1. read current value of j          1. increment j
2. assign that OLD value to LHS     2. assign that NEW value to LHS
3. increment j                      (done)
```

> **Interview-gold line:** *"Postfix returns the value before incrementing; prefix returns the value after incrementing. Both still increment the variable itself — the only difference is what gets handed back to whatever expression is using it."* This matters heavily inside loop headers and array-index expressions (`arr[i++]` vs `arr[++i]`), covered later when loops/arrays are introduced.

```java
// WRONG (common confusion): assuming j++ and ++j always behave identically
int x = arr[i++]; // uses arr[i], THEN increments i
int y = arr[++i]; // increments i FIRST, THEN uses arr[i] — different index entirely!
```



### 2. Relational Operators

```
==   equal to
!=   not equal to
<    less than
>    greater than
<=   less than or equal to
>=   greater than or equal to
```

**Every relational operator produces a `boolean` result** (`true`/`false`) — never anything else.

```java
int a = 5, b = 10;
boolean c = (a == b);   // false — "is a equal to b?"
boolean d = (a != b);   // true  — "is a not equal to b?"
boolean e = (a < b);    // true
boolean f = (a > b);    // false
boolean g = (a <= b);   // true  — true if EITHER < or ==
boolean h = (a >= b);   // false
```

#### `=` vs `==` — the classic gotcha

```java
// WRONG (a very common bug in many C-family languages, though Java's compiler
// actually catches this specific case when the types don't line up as boolean):
if (a = b) { ... }   // this is ASSIGNMENT (a becomes b's value), not comparison

// RIGHT:
if (a == b) { ... }  // this is COMPARISON — "is a equal to b?"
```

- `a = b` → an **assignment**: "take whatever `b` holds and put it into `a`." After this line, `a`'s value has *changed* to match `b`.
- `a == b` → a **comparison**: "are `a` and `b` currently equal?" Neither variable's value changes; you get back a `boolean`.

> Whatever you're evaluating with `==`, `!=`, `<`, `>`, `<=`, `>=` — the whole thing is called an **expression**, and its result-type here is always `boolean`. This vocabulary (expression, operand, operator) matters for later topics like conditionals and loops.



### 3. Bitwise Operators

These operate at the **bit level** (0s and 1s), not on the decimal value directly.

```
&    bitwise AND
|    bitwise OR
^    bitwise XOR (exclusive OR)
~    bitwise NOT (unary — operates on ONE operand)
<<   left shift
>>   right shift (arithmetic / "sign-preserving")
>>>  right shift with zero fill (logical / "sign-ignoring")
```

Each also has a compound form: `&=`, `|=`, `^=`, `<<=`, `>>=`, `>>>=` (same pattern as `+=`).

#### Truth table (AND, OR, XOR, NOT)

| A | B | A & B | A \| B | A ^ B | ~A |
|-|-|-|-|-|-|
| 0 | 0 | 0 | 0 | 0 | 1 |
| 0 | 1 | 0 | 1 | 1 | 1 |
| 1 | 0 | 0 | 1 | 1 | 0 |
| 1 | 1 | 1 | 1 | 0 | 0 |

- **AND (`&`):** result is `1` only if **both** bits are `1`.
- **OR (`|`):** result is `1` if **at least one** bit is `1`.
- **XOR (`^`):** result is `1` if the number of `1`s among the operands is **odd** (for two single bits, this means "exactly one is 1, not both").
- **NOT (`~`):** unary — flips every bit (`0`→`1`, `1`→`0`).

> This is the same truth table used in digital logic gates — a processor's ALU is built from exactly these gates operating on 0s and 1s. Bitwise operators are the programming-language-level expression of the same hardware concept (Lecture 1's ISA territory).

#### Worked example

```java
byte a = 2;   // 00000010
byte b = 3;   // 00000011

int c = a & b;   // 00000010 → 2
int d = a | b;   // 00000011 → 3
int e = a ^ b;   // 00000001 → 1
int f = ~a;      // 11111101 → -3 (flips ALL 32 bits after promotion — see below)
```

> Note the result types are all `int`, not `byte` — this is **type promotion** in action (from Lecture 5): `byte`/`short`/`char` operands are always promoted to `int` before any calculation, bitwise operations included.



### 4. Bitwise Shift Operators — Deep Dive

This is the section most tutorials gloss over. The key fact that changes everything:

> **Shift operations (`<<`, `>>`, `>>>`) ONLY actually execute on `int` or `long` — never directly on `byte` or `short`.** If you write a shift expression on a `byte`/`short` variable, Java **first promotes it to `int`**, performs the *entire* shift on that full 32-bit representation, and only truncates back down to 8/16 bits at the very end (if you cast it back).

#### Left Shift (`<<`) — the basic mechanics

```java
byte b = 8;   // 00000001000... wait, as a byte: 00001000
b = (byte)(b << 1);
```

Every bit moves one position left; a new `0` enters from the right (LSB side); whatever falls off the left (MSB side) is discarded.

```
Before: 00001000  (8)
After:  00010000  (16)
```

**Left shift by `n` is equivalent to multiplying by `2ⁿ`** — because moving a `1` bit one position left doubles the positional value it represents (e.g., the "8s place" bit moving into the "16s place").

```java
b << 1   // equivalent to b * 2
b << 2   // equivalent to b * 4  (2^2)
b << 3   // equivalent to b * 8  (2^3)
```

#### The trap: shifting far enough flips the sign — but only if you truncate early

If you keep left-shifting a `byte` and **truncate back to 8 bits after every shift**, you'll eventually push a `1` into the byte's MSB (bit 7), making the truncated value appear negative — even though the "real" number (still alive in the full 32-bit `int` representation before truncation) is not actually negative yet.

```java
byte b = 1;                  // 00000001
b = (byte)(b << 7);          // truncated to 8 bits: 10000000 → decodes as -128 (!)
```

**But if you don't truncate after every step** — i.e., you keep working in `int` — the "negative" appearance never happens prematurely, because the sign only depends on the **32-bit int's own MSB (bit 31)**, not on where the `1` happens to sit within the lowest 8 bits.

```java
int i = 1;
i = i << 7;      // as a genuine 32-bit int: still clearly positive (128)
byte b = (byte) i;   // ONLY NOW do we truncate to 8 bits → -128
```

> **Interview-gold insight:** *"A byte that appears to 'go negative' after repeated left shifts isn't actually behaving differently from an int — it's an artifact of premature truncation. The underlying shift always happens on the full-width int, and only casting back to byte reveals (or hides) the sign flip, because the byte's MSB is really just bit 7 of a much longer int."*

#### How far can you shift before hitting a wall? — the modulo-32 rule

Since actual shifting happens on a 32-bit `int`, you can meaningfully shift the LSB all the way up to the MSB — that's **31 positions** (bit 0 → bit 31). Beyond that, there's nowhere left to go.

Java's solution: **shift amounts on `int` are always taken modulo 32.**

```java
int i = 1;
i << 32   // equivalent to i << (32 % 32) = i << 0  → i unchanged!
i << 33   // equivalent to i << (33 % 32) = i << 1  → same as shifting once
```

```
i << 31   → smallest possible int value (Integer.MIN_VALUE) — the 1 has reached the MSB
i << 32   → back to the ORIGINAL value (mod 32 = 0, no shift happens at all)
i << 33   → same as i << 1
```

For `long` (64-bit), the equivalent rule is **modulo 64**, for the same underlying reason (a `long` has 64 bit-positions, so the LSB can travel at most 63 places before hitting the MSB).

> **Why Java does this instead of just producing zero/garbage past the bit width:** performance. Once a bit has traveled from LSB to MSB, further shifting in the "naive" sense would just produce meaningless all-zero results — there's no useful computation left to do. Rather than waste processor cycles simulating an operation that's mathematically pointless past 31/63 shifts, Java wraps the shift amount via modulo so the operation stays fast and well-defined.

#### Right Shift (`>>`) — arithmetic / sign-preserving

Moves every bit one position right; the vacated MSB slot is filled with a **copy of the original sign bit** (not always `0`) — this preserves the number's sign through the shift.

```
Positive example:
Before: 00001000  (8)
After:  00000100  (4)     — new bit introduced at MSB is 0 (was already positive)

Negative example:
Before: 10000000  (-128, byte)
After:  11000000  (-64)   — new bit introduced at MSB is 1 (preserves negativity)
```

**Right shift by `n` is equivalent to dividing by `2ⁿ`** (with the sign preserved) — same logic as left shift, mirrored.

```java
b >> 1   // equivalent to b / 2  (sign-preserving)
```

> Right shift on `byte`/`short` also always truly executes on the promoted `int` first, exactly like left shift — the same modulo-32/64 capping and truncation-after-the-fact rules apply identically.

#### Right Shift with Zero Fill (`>>>`) — "the dumb shift"

Identical mechanics to `>>`, **except the newly introduced MSB bit is ALWAYS `0`**, regardless of what the original sign bit was.

```
Same negative starting value: 10000000  (-128 as a byte, if we ignore promotion for illustration)
>>  (normal):     11000000  (-64)   — preserves the sign
>>> (zero-fill):  01000000  (+64)   — does NOT preserve the sign, forces positive
```

> **Practical use case:** whenever a value should never conceptually be negative — e.g., pixel color values in image processing — `>>>` guarantees the shift never accidentally reintroduces a sign bit, because it *always* fills with `0` no matter what was there originally.

```java
// WRONG use case: sign-sensitive arithmetic where you actually need to preserve negativity
int result = negativeValue >>> 1;  // silently discards the sign — a real bug if unintended

// RIGHT: use >> when sign matters, >>> only when you specifically want an unsigned interpretation
int result = negativeValue >> 1;   // preserves sign correctly
```



### 5. Logical Operators — `&&` and `||`

These look similar to bitwise `&` / `|` but operate on **whole boolean expressions**, not individual bits.

```java
int a = 5, b = 10, c = 15;
boolean d = (a < b) && (a < c);   // "is a<b AND a<c" — true only if BOTH are true
boolean e = (a < b) || (a < c);   // "is a<b OR a<c"  — true if AT LEAST ONE is true
```

Same truth-table logic as bitwise AND/OR, just applied to `true`/`false` operands instead of `0`/`1` bits.

#### Short-Circuit Evaluation — the key behavior of `&&` / `||`

> **`&&`:** if the LEFT operand evaluates to `false`, the RIGHT operand is **never evaluated** — the overall result is already guaranteed `false` regardless of what the right side would produce.
>
> **`||`:** if the LEFT operand evaluates to `true`, the RIGHT operand is **never evaluated** — the overall result is already guaranteed `true` regardless of the right side.

```java
if (a < b && b < c) { ... }
// If (a < b) is false, Java NEVER evaluates (b < c) at all.
```

**Why this matters in real code:** short-circuiting lets you safely chain a "guard" condition before a condition that would otherwise error or waste work — the canonical example (covered in a later lecture) is null-checking:

```java
if (obj != null && obj.someMethod()) { ... }
// If obj IS null, the right side (which would throw an error on a null object)
// is never even reached, because short-circuiting stops evaluation immediately.
```

#### The overlooked distinction: bitwise `&` / `|` on booleans do NOT short-circuit

Java technically **allows** using the single-character bitwise operators (`&`, `|`) directly on `boolean` expressions too — a fact many developers (even ones who understand short-circuiting) don't realize:

```java
boolean d = (a < b) & (a < c);   // valid Java! Same logical AND result as &&...
```

...**but with one crucial difference: `&` and `|` always evaluate BOTH sides, no matter what** — they never short-circuit.

```java
// WRONG (if short-circuiting was the intent): using & instead of &&
if (obj != null & obj.someMethod()) { ... }
// Even if obj IS null, Java still evaluates obj.someMethod() — likely crashing the program!

// RIGHT: use && when you need (or might need) the safety of short-circuiting
if (obj != null && obj.someMethod()) { ... }
```

> **Interview-gold line, and a genuine differentiator question:** *"`&&`/`||` short-circuit; `&`/`|` do not, even when applied to plain boolean expressions instead of bits — Java allows both syntaxes on booleans, but they're not interchangeable if right-side evaluation has side effects or could throw."* In real-world code, `&&`/`||` are used essentially 100% of the time; using `&`/`|` on booleans is a rare, deliberate choice (e.g., when you *specifically want* both sides evaluated regardless, perhaps because both sides have needed side effects).



### 6. Assignment Operator (`=`)

Already used throughout — formalizing it:

```java
int a = 5;   // "take the literal 5 (right side) and store it into a (left side)"
```

#### Chained assignment

```java
int a, b, c;
a = b = c = 10;
```

Evaluates **right to left**: `10` → assigned to `c` first → `c`'s value assigned to `b` → `b`'s value assigned to `a`. All three end up holding `10`. This is shorthand for writing three separate declaration statements.



### 7. Ternary Operator — Preview Only

```java
condition ? valueIfTrue : valueIfFalse
```

The **only operator in Java that takes three operands** (hence "ternary") — contrast with **unary** operators (one operand, e.g., `~a`, `i++`) and **binary** operators (two operands, e.g., `a + b`). Full treatment deferred to the upcoming conditionals/`if-else` lecture, where its use case actually clicks.



### 8. Operator Precedence

Java follows a BODMAS-like precedence order (highest to lowest, roughly):

```
Postfix (x++, x--)
Prefix / unary (++x, --x, ~x, !x)
Multiplicative (*, /, %)
Additive (+, -)
Shift (<<, >>, >>>)
Relational (<, >, <=, >=)
Equality (==, !=)
Bitwise AND (&)
Bitwise XOR (^)
Bitwise OR (|)
Logical AND (&&)
Logical OR (||)
Assignment (=, +=, -=, etc.)   ← LOWEST precedence
```

- Operators in the same tier evaluate **left to right** (except assignment, which evaluates **right to left** — see chained assignment above).
- **`=` has the lowest precedence of all** — logical, since nothing should get assigned until every other part of the expression on the right has been fully resolved.

#### Practical advice: don't memorize the table — use parentheses

> **Golden rule:** in real production code, nobody mentally walks through a precedence table for a complex expression — you use **explicit parentheses** to force the evaluation order you want, both for correctness and for readability by whoever reads the code next (including future you).

```java
int result = b + c * d;     // evaluates as b + (c * d), per standard precedence
int result2 = (b + c) * d;  // parentheses override precedence — now b+c happens FIRST
```

> Parentheses effectively have the **highest precedence of all** — they're how you take explicit control rather than relying on implicit rules.



#### Quick Self-Check

> **Q1.** What's the difference in output between `int k = j++;` and `int l = ++j;` if `j` starts at 9?

*Answer:* `k = j++` gives `k = 9` (old value), then `j` becomes 10. `l = ++j` first makes `j = 11`, then `l = 11` (new value). Both increment `j`, but they hand back different values to the assignment.

> **Q2.** Why does `byte b = 1; b = (byte)(b << 7);` produce `-128`, but the "same" shift on an `int` doesn't go negative until shift amount 31?

*Answer:* Because the actual shift always executes on a promoted 32-bit `int`. On a `byte`, the truncation back to 8 bits happens immediately after just one shift, so a `1` reaching bit 7 (the byte's own MSB) flips its sign at that point. On a genuine `int`, the sign only depends on bit 31, so the same `1` bit has to travel much further (31 positions) before the number appears negative.

> **Q3.** Why does `i << 32` return the original, unshifted value of `i`?

*Answer:* Shift amounts on `int` are taken modulo 32. `32 % 32 = 0`, so `i << 32` is equivalent to `i << 0` — no shift at all.

> **Q4.** What's the practical risk of writing `if (obj != null & obj.getValue() > 0)` instead of using `&&`?

*Answer:* `&` does not short-circuit — both sides are always evaluated. If `obj` is `null`, `obj.getValue()` still gets called and would throw a `NullPointerException`, defeating the entire purpose of the null-check.



#### Golden Rules / Checklist

- [ ] `=` is **assignment** ("put right side into left side"); `==` is **comparison** ("are these equal?"), returning a `boolean`. Never confuse the two.
- [ ] Postfix (`x++`) returns the **old** value then increments; prefix (`++x`) increments **first** then returns the **new** value.
- [ ] All relational operators (`==`, `!=`, `<`, `>`, `<=`, `>=`) always produce a `boolean` result — never anything else.
- [ ] Bitwise `&`, `|`, `^`, `~` operate on individual bits, following the standard AND/OR/XOR/NOT truth table; `byte`/`short` operands are always promoted to `int` first (per Lecture 5's promotion rules).
- [ ] **Shift operators (`<<`, `>>`, `>>>`) only genuinely execute on `int`/`long`.** On `byte`/`short`, the value is promoted to `int`, the full shift happens there, and only a final cast truncates it — this is *why* small-width shifted values can appear to "unexpectedly" flip sign.
- [ ] Left shift `<<n` ≡ multiply by `2ⁿ`. Right shift `>>n` (sign-preserving) ≡ divide by `2ⁿ`, keeping the original sign.
- [ ] `>>` fills the new MSB with a **copy of the original sign bit** (preserves sign); `>>>` always fills with **`0`** (forces a positive/unsigned interpretation) — use `>>>` only when negativity is conceptually meaningless (e.g., raw pixel/color data).
- [ ] Shift amounts wrap via **modulo 32** for `int`, **modulo 64** for `long` — you cannot shift a bit further than the type's own bit-width, and Java caps the amount rather than producing meaningless results.
- [ ] `&&`/`||` **short-circuit** (skip evaluating the right side once the result is already determined); the single-character `&`/`|` do **not** short-circuit, even when used on plain `boolean` expressions — this is a real and easy-to-miss source of bugs (e.g., null-check patterns).
- [ ] Chained assignment (`a = b = c = 10;`) evaluates **right to left**.
- [ ] `=` has the **lowest** operator precedence of all; parentheses have the **highest** — in real code, use explicit parentheses rather than relying on memorized precedence tables.
- [ ] Ternary (`? :`) is the only operator with **three** operands — full treatment deferred to conditionals.



#### Practice Questions

**Basic**
1. What does `x %= 3;` expand to?
2. Given `int p = 5;`, what are the values of `p` and the printed result after `System.out.println(p--);`?
3. Write the truth table row for `A ^ B` when `A = 1, B = 1`.

**Intermediate**
4. Explain why `byte b = 100; b = (byte)(b << 1);` produces a negative number, walking through the promotion and truncation steps.
5. What is `int i = 5; i = i << 34;` equivalent to, and why?
6. A developer writes `if (index >= 0 & index < array.length)`. What's the practical risk compared to using `&&`, even though the logical result is the same in normal cases?
7. Explain the difference between `>>` and `>>>` when right-shifting a negative number.

**Advanced / Interview-style**
8. Trace through, bit by bit, what happens internally when you execute `byte b = 1; int i = b << 7;` (note: assigning to an `int`, NOT casting back to `byte`) — what value does `i` actually hold, and why is it different from what you'd get by immediately casting the shift result to `byte`?
9. Why does Java cap shift amounts via modulo (32 for int, 64 for long) instead of simply defining the behavior as "shift out to zero" for amounts beyond the bit width?
10. A candidate claims "bitwise `&` and logical `&&` always produce identical results, so it doesn't matter which one I use." Under what circumstances would this claim be false, and why?
11. Explain, using the concept of "significance" (MSB vs LSB) from Lecture 4, why right-shifting is equivalent to division and left-shifting is equivalent to multiplication, tying it back to two's complement representation for negative operands.



## 7. Flow of Control & Selection Statements (if / switch)



### 1. Flow of Control — The Concept

**Flow of control** = the order in which statements in your compiled program actually execute at runtime.

- Default behavior: **sequential** — line 1, then line 2, then line 3, and so on, matching the interpreter/JIT execution model from Lecture 2 (bytecode read and executed line by line, or hot-path compiled).
- Certain Java keywords **deliberately break this default sequential flow** and introduce an alternate path. These fall into three categories:

```
Flow of Control
├── Selection   (if, if-else, if-else-if ladder, switch)
├── Iteration   (loops — for, while, do-while — covered next lecture)
└── Jump        (break, continue, return — covered in a later lecture)
```

This lecture covers **Selection** in full.



### 2. The `if` Statement

#### Conceptual framing

Think of it as a fork in a path: you're walking a straight line (sequential flow), then you hit a decision point where the path splits based on some condition (e.g., "do I have a lot of time? take the long, scenic road; short on time? take the fast, bumpy one"). That decision-making, translated into code, is a **selection statement**.

#### Syntax

```java
if (expression) {
    // do something
}
```

- `expression` must evaluate to a `boolean` — `true` or `false`. Nothing else is valid here.
- If `true`: the block executes.
- If `false`: the block is skipped entirely, and control moves to whatever comes after it.

```java
boolean b = true;
if (b == true) {
    // this executes because b is true
}
```

```java
int i = 5;
if (i == 5) {
    System.out.println("i is 5");
}
System.out.println("i is not 5");   // WRONG placement if you intend mutual exclusivity —
                                      // this line ALWAYS runs regardless of the if above,
                                      // because it's not inside an else block
```

> **Common beginner mistake:** writing a plain `if` followed by an unrelated `println` and expecting only one of the two to run. A bare `if` with no `else` does **not** create mutual exclusivity — the code after it always runs regardless of whether the `if` block executed.



### 3. `if-else`

```java
if (i == 5) {
    System.out.println("i is 5");
} else {
    System.out.println("i is not 5");
}
```

Now there are exactly **two mutually exclusive paths**: if the expression is `true`, the `if` block runs and the `else` block is skipped; if `false`, the reverse.

Any boolean-producing expression can go in the condition — relational operators, logical operators, or combinations:

```java
if (i > 5 && i < 10) {   // combines relational + logical operators
    // i is strictly between 5 and 10
}
```

#### Determining odd/even — a classic `if-else` use case

```java
if (i % 2 == 0) {
    System.out.println("i is even");
} else {
    System.out.println("i is odd");
}
```

Uses the modulus operator (Lecture 6) — remainder `0` when divided by 2 means even; remainder `1` means odd.

#### Curly braces are optional for single-statement blocks

```java
// WRONG (inconsistent style, though technically legal):
if (i % 2 == 0)
    System.out.println("i is even");
else
    System.out.println("i is odd");

// RIGHT (good practice): always use braces, even for one-line blocks
if (i % 2 == 0) {
    System.out.println("i is even");
} else {
    System.out.println("i is odd");
}
```

> **Golden rule:** braces are only strictly *required* when a block has more than one statement — but always including them is considered good practice, since it protects you the moment you (or someone else) later add a second statement to that block without noticing the missing braces.



### 4. Nested `if`

An `if` (or `if-else`) placed **inside** another `if`/`else` block.

```java
if (i > 5) {
    if (i < 10) {
        // runs only if i is BOTH > 5 AND < 10
    } else {
        // runs if i > 5 but NOT < 10 (i.e., i >= 10)
    }
} else {
    // runs if i <= 5
}
```

**Key facts about nesting:**
- Java allows nesting to **any depth** — but **good practice caps it at 2 levels max**. Beyond that, code readability collapses (you have to mentally track which `else` belongs to which `if`, several layers deep).
- Every `if`/`else if` can have its **own independent `else`**, or none at all. An inner `if` without an `else` simply does nothing when its condition is `false` — execution just falls through to whatever comes after that inner block.

#### Prefer combining conditions with logical operators over deep nesting

```java
// WRONG (unnecessarily nested, harder to read):
if (i > 5) {
    if (i < 10) {
        // do something
    }
}

// RIGHT (flattened using a logical AND):
if (i > 5 && i < 10) {
    // do something
}
```

> **Interview/code-review line:** *"Prefer combining conditions with `&&`/`||` over nesting whenever the nested logic is just a compound boolean check — it's functionally identical but far more readable."*



### 5. `if-else-if` Ladder

Handles **more than two mutually exclusive choices** — a generalization of `if-else` for N branches.

```java
if (i == 5) {
    System.out.println("i is 5");
} else if (i == 6) {
    System.out.println("i is 6");
} else if (i == 7) {
    System.out.println("i is 7");
} else if (i == 8) {
    System.out.println("i is 8");
} else if (i == 9) {
    System.out.println("i is 9");
}
```

#### Critical behavior: evaluation stops at the first `true` condition

Once a condition in the ladder evaluates `true`, its block executes, and **every subsequent condition in the ladder is skipped entirely — not even evaluated**. Control jumps straight past the rest of the ladder.

```
Check condition 1 → false → skip
Check condition 2 → false → skip
Check condition 3 → TRUE  → execute this block, then EXIT THE ENTIRE LADDER
(conditions 4, 5, ... are never even checked)
```

#### The optional trailing `else` — the catch-all

```java
if (age > 80) {
    System.out.println("You are very old");
} else if (age > 60) {
    System.out.println("You are old");
} else if (age > 40) {
    System.out.println("You are becoming old");
} else if (age > 20) {
    System.out.println("You are young");
} else {
    System.out.println("You are a child");
}
```

The final `else` (no condition) catches every case not matched by any prior branch above it.

#### Why the ladder matters — mutual exclusivity is the whole point

```java
// WRONG: separate independent if statements when you actually want a ladder
if (age > 80) { System.out.println("very old"); }
if (age > 60) { System.out.println("old"); }        // this ALSO fires if age > 80!
if (age > 40) { System.out.println("becoming old"); } // this ALSO fires if age > 60!
// For age = 50, this WRONGLY prints BOTH "old" AND "becoming old"

// RIGHT: use else-if to guarantee only ONE branch fires
if (age > 80) { System.out.println("very old"); }
else if (age > 60) { System.out.println("old"); }
else if (age > 40) { System.out.println("becoming old"); }
// For age = 50, this correctly prints ONLY "becoming old"
```

> **Interview-gold insight:** *"A chain of independent `if` statements checks every condition regardless of earlier outcomes — a real bug source when the conditions overlap (e.g., `age > 60` and `age > 40` are both true for age=50). An `if-else-if` ladder guarantees mutual exclusivity: once one branch fires, none of the later ones are even evaluated."*



### 6. Switch Statements

A **second selection construct**, functionally similar to an `if-else-if` ladder in many cases — but with a different syntax, different constraints, and (in the right circumstances) genuinely better performance.

#### Syntax

```java
switch (i) {
    case 1:
        System.out.println("i is 1");
        break;
    case 2:
        System.out.println("i is 2");
        break;
    case 3:
        System.out.println("i is 3");
        break;
    default:
        System.out.println("i is greater than 3");
        break;
}
```

**Mapping to `if-else-if`:**
```
switch (i) { case 1: ...; break; case 2: ...; break; default: ...; }
     ↕ equivalent to ↕
if (i == 1) { ... } else if (i == 2) { ... } else { ... }
```

`switch (i)` says: *"compare `i` against each `case` value."* Each `case` is essentially an equality check against the switch expression. `default` is the equivalent of the ladder's trailing catch-all `else`.

#### The role of `break`

Without `break`, execution **falls through** into the next case's code — regardless of whether that next case's value matches — continuing until it hits a `break` or runs out of cases entirely.

```java
// WRONG (if you intend each case to be independent): omitting break
switch (i) {   // i = 1
    case 1:
        System.out.println("i is 1");   // prints
        // NO break here!
    case 2:
        System.out.println("i is 2");   // ALSO prints — fell through!
    case 3:
        System.out.println("i is 3");   // ALSO prints — fell through again!
        break;
    default:
        System.out.println("i is greater than 3");
}
// Output for i=1: "i is 1", "i is 2", "i is 3"  — likely NOT what was intended
```

```java
// RIGHT: break after each case's logic, to stop fall-through
switch (i) {
    case 1:
        System.out.println("i is 1");
        break;
    case 2:
        System.out.println("i is 2");
        break;
    ...
}
```

> **Why fall-through exists at all (it's a deliberate design, not a flaw):** sometimes you genuinely *want* multiple cases to execute in sequence once a matching case is hit — e.g., grouping several case labels to share the same logic block. `break` gives you explicit control over whether execution stops at a single case or continues into the next one.

#### Constraints unique to `switch` (vs. `if-else-if`)

1. **The switch expression must evaluate to `byte`, `short`, `int`, `char`, or (`String` since JDK 7, and `enum`).** — Nothing else. In particular, `boolean` is **not** allowed.
   ```java
   switch (i > 4) { ... }   // WRONG: compile error — "cannot switch on a value of type boolean"
   ```
   An `if-else-if` ladder has no such restriction — any boolean-evaluable expression works there, including inequalities (`>`, `<`, `<=`, `>=`), ranges, and complex logical combinations.

2. **Only equality checks are possible** — every `case` implicitly means "is the switch value **equal to** this literal?" You cannot write `case > 4:` or any inequality inside a `case` label. An `if-else-if` ladder, by contrast, can test **any** relational/logical condition (`<`, `>`, ranges, compound booleans), not just equality.

3. **No duplicate case values allowed.**
   ```java
   // WRONG:
   switch (i) {
       case 1: ...; break;
       case 1: ...; break;   // compile error: duplicate case label
   }
   ```
   By contrast, an `if-else-if` ladder technically *allows* you to repeat the same condition twice — the second occurrence simply becomes unreachable dead code (since the ladder stops at the first match), but the compiler won't flag it as an error the way `switch` does.

#### Post-JDK enhancements (context, not deep-dived here)

- **JDK 7+:** `String` values became valid switch expressions (in addition to the primitive types listed above).
- **JDK 14+:** Switch received major syntactic enhancements (arrow syntax, switch expressions returning values, pattern matching, etc.) — deferred to a dedicated later lecture once the JDK-8-era "core" fundamentals of the language are complete. Traditional `switch` (as covered here) remains fully valid and supported today; the newer syntax is additive, not a replacement.



### 7. Switch vs. if-else-if Ladder — Why Both Exist

Two functional differences, then the real performance story.

#### Difference 1: Equality vs. arbitrary boolean expressions

```
switch        → equality ONLY (case value == switch expression)
if-else-if    → equality AND inequality (any boolean-producing expression)
```

This is *why* switch can't replace the ladder entirely — anything requiring `<`, `>`, ranges, or compound conditions simply cannot be expressed as a switch.

#### Difference 2 (the real reason switch exists): performance via Jump Tables

**The naive assumption (partially wrong):** many learners assume `switch` internally just becomes an `if-else-if` ladder under the hood — checking `case 1`, then `case 2`, then `case 3` sequentially until a match. **This is not what actually happens** when the compiler can optimize it.

**What the compiler (`javac`)/JVM actually does:** for a `switch`, the compiler builds an internal structure called a **jump table** — conceptually similar to an array — that lets execution **jump directly** to the matching case's code, without sequentially testing every earlier case first.

```
Jump Table (conceptual):
Index 0 → code for case 1
Index 1 → code for case 2
Index 2 → code for case 3
Index 3 → code for default
```

If `switch(i)` is evaluated and `i == 2`, the compiler-generated logic computes an offset (e.g., `i - 1`) and **jumps straight to index 1** — the code for `case 2` — without ever comparing `i` to `1` or `3` first.

```
if-else-if ladder:  O(n) — worst case, evaluate ALL n conditions sequentially
switch (jump table): O(1) — direct jump, regardless of how many cases exist
```

> **Interview-gold summary:** *"A well-formed switch with dense case values compiles to a jump table, giving O(1) case selection versus an if-else-if ladder's O(n) sequential evaluation — this is the real reason switch exists as a distinct construct, not just syntactic sugar."*

#### Jump tables are NOT always built — it depends on case density

The jump table only makes sense when case values are **dense** (close together, e.g., `1, 2, 3, 4`). If case values are **sparse** (e.g., `1, 1000, 100000`), building an array-like table spanning the full range from the smallest to largest case would be enormously wasteful of memory (a table sized to fit case `100000` even though only 3 cases actually exist).

**Java's compiler chooses between two internal strategies:**

| Strategy | When Used | Mechanism | Complexity |
|-|-|-|-|
| **`tableswitch`** | Case values are dense | Direct array-like jump table, indexed by `(value - offset)` | O(1) |
| **`lookupswitch`** | Case values are sparse | A compact table of only the (value, target) pairs that actually exist, searched via **binary search** | O(log n) |

```java
// Dense example → compiler generates a tableswitch
switch (i) {
    case 1: ...; break;
    case 2: ...; break;
    case 3: ...; break;
}

// Sparse example → compiler generates a lookupswitch
switch (i) {
    case 1: ...; break;
    case 1000: ...; break;
    case 100000: ...; break;
}
```

Both `tableswitch` and `lookupswitch` are **still faster than a naive sequential if-else-if ladder** (O(1) or O(log n) respectively, versus O(n)) — the compiler simply picks whichever strategy fits the actual case distribution, rather than wastefully building a huge sparse array.

> **Interview-gold line for the advanced follow-up:** *"When case values are sparse, Java doesn't force a giant, mostly-empty jump table — it falls back to a `lookupswitch`, which does a binary search over just the actual case values, still beating a linear if-else-if scan."*



### 8. Nested `switch`

Just like nested `if`, a `switch` can contain another `switch` inside any of its case blocks, to any depth (though, as with nested `if`, deep nesting hurts readability and should be minimized).

```java
switch (j) {
    case 4:
        System.out.println("j is 4");
        break;
    case 5:
        switch (k) {          // nested switch inside case 5
            case 1:
                // ...
                break;
            case 2:
                // ...
                break;
        }
        break;
    default:
        System.out.println("j is not 4 and not 5");
}
```

Use case: when handling one case genuinely requires its own independent multi-way branching — but if it doesn't, prefer flattening the logic rather than nesting for its own sake.



#### Quick Self-Check

> **Q1.** Why does omitting `else` between two `if` blocks risk incorrect behavior when the two conditions overlap (e.g., `age > 60` and `age > 40`)?

*Answer:* Without `else`, both `if` statements are evaluated independently — if both conditions happen to be true for the same value (e.g., age=70 satisfies both `>60` and `>40`), both blocks execute, likely printing more than one message when only one was intended. An `if-else-if` ladder guarantees only one branch fires.

> **Q2.** Why can't you write `switch (age > 18)` in Java?

*Answer:* A switch expression must evaluate to `byte`, `short`, `int`, `char` (or `String`/`enum` since later JDKs) — never `boolean`. `age > 18` evaluates to a `boolean`, which switch cannot accept.

> **Q3.** Why does Java sometimes use a `lookupswitch` instead of a `tableswitch`?

*Answer:* `tableswitch` requires building a dense, array-like jump table spanning the full range from the smallest to largest case value — efficient only when cases are close together. If cases are sparse (e.g., 1, 1000, 100000), that table would be enormous and mostly wasted space, so the compiler instead generates a `lookupswitch`, which binary-searches a compact table of just the actual case values.



#### Golden Rules / Checklist

- [ ] **Flow of control** = the order statements execute; default is sequential; Selection/Iteration/Jump constructs deliberately alter it.
- [ ] `if` condition must evaluate to `boolean` — nothing else.
- [ ] A bare `if` with no `else` does **not** create mutual exclusivity with the code after it — that code always runs regardless.
- [ ] `if-else` gives exactly two mutually exclusive branches; braces are technically optional for single-statement blocks but should always be used as good practice.
- [ ] Prefer combining conditions with `&&`/`||` over deep nesting when the nested logic is just a compound boolean check; cap real nesting at ~2 levels for readability.
- [ ] **`if-else-if` ladder** guarantees mutual exclusivity — once a condition is `true`, all later conditions in the ladder are skipped, not even evaluated. Independent `if` statements do NOT have this guarantee.
- [ ] **`switch`** can only test **equality** against `byte`/`short`/`int`/`char` (or `String`/`enum` post-JDK-7) — never `boolean`, never inequalities like `<`/`>`.
- [ ] **`break`** stops fall-through in a switch case; omitting it causes execution to continue into subsequent cases regardless of their match — sometimes intentional, usually a bug if forgotten.
- [ ] Duplicate `case` values are a compile error in `switch`; a duplicated condition in an `if-else-if` ladder is legal but becomes unreachable dead code.
- [ ] **Switch's real performance advantage over if-else-if**: the compiler builds a **jump table**, giving O(1) (`tableswitch`, dense cases) or O(log n) (`lookupswitch`, sparse cases, via binary search) case selection — versus the ladder's O(n) sequential evaluation.
- [ ] Nested `switch`/nested `if` are both legal to any depth, but should be avoided beyond 1–2 levels for readability; flatten with combined boolean conditions where possible.



#### Practice Questions

**Basic**
1. What data types are valid for a `switch` expression (pre-JDK 7)?
2. What does forgetting `break` in a switch case cause?
3. Write an `if-else-if` ladder that prints "negative", "zero", or "positive" based on an int variable `n`.

**Intermediate**
4. Explain why the following code prints more than one message for `age = 70`, and rewrite it to fix the bug:
   ```java
   if (age > 60) { System.out.println("old"); }
   if (age > 40) { System.out.println("getting old"); }
   ```
5. Why can't you replace `if (score >= 90) {...} else if (score >= 80) {...}` with an equivalent `switch` statement directly?
6. Explain the difference between a `tableswitch` and a `lookupswitch`, and what property of the case values determines which one the compiler generates.

**Advanced / Interview-style**
7. Explain, using time complexity, exactly why a `switch` with dense integer cases is faster than the equivalent `if-else-if` ladder for a large number of cases.
8. A colleague says "switch is just syntactic sugar for if-else-if, so it never actually improves performance." Correct this claim using the jump-table concept, and describe under what specific condition the claim would actually be true (i.e., when might switch offer no meaningful performance benefit).
9. Why might a compiler choose NOT to build a `tableswitch` even when there are relatively few `case` labels, if those labels have very large gaps between their values (e.g., `case 1`, `case 1000000`)?
10. Design a scenario (in plain English, no code needed) where nested `switch` statements would be a legitimate, readable choice rather than an anti-pattern.



## 8. Iteration Statements (Loops) & Jump Statements



### 1. Why Loops Exist

Without loops, repeating an action N times means writing N nearly-identical statements by hand:

```java
// WRONG (does not scale): manually repeating statements
System.out.println(1);
System.out.println(2);
System.out.println(3);
// ...repeated by hand up to 10 — utterly impractical for large N
```

**Iteration statements (loops)** let a block of code repeat automatically, based on a condition, without hand-duplicating it. Java has three loop constructs:

```
Iteration
├── while loop
├── do-while loop
└── for loop
```

All three ultimately do the same conceptual thing (repeatedly execute a block while some condition holds) — they differ in **when** the condition is checked and how the syntax packages the setup/condition/update logic.



### 2. `while` Loop

#### Syntax

```java
while (expression) {
    // do something
}
```

- `expression` must evaluate to `boolean` — identical requirement to `if`.
- **Condition is checked BEFORE each iteration** (including the very first one). If `false` on the very first check, the loop body never executes at all.

#### Worked example — print 1 to 10

```java
int i = 1;
while (i <= 10) {
    System.out.println(i);
    i++;
}
```

```
Check i <= 10 → true  → print i, then i++
Check i <= 10 → true  → print i, then i++
... repeats ...
Check i <= 10 → false (i is now 11) → EXIT loop
```

#### The infinite loop trap

If the loop variable is never updated toward the exit condition, the condition stays `true` forever:

```java
// WRONG: no increment — condition never becomes false → infinite loop, eventually crashes/hangs
int i = 1;
while (i <= 10) {
    System.out.println(i);
    // missing i++ !
}
```

> **Every loop needs a genuine path to making its condition false eventually** — this is the single most important rule to internalize before writing any loop.

#### Running it in reverse

```java
int i = 10;
while (i >= 1) {
    System.out.println(i);
    i--;
}
```
Direction is entirely controlled by the initial value, the comparison operator, and whether you increment or decrement — nothing else about `while` changes.

#### A subtle off-by-one trap: combining the condition and the update

```java
// WRONG: comparing i++ instead of i, causes an off-by-one shift
int i = 1;
while (i++ <= 10) {
    System.out.println(i);   // prints 2 through 11, NOT 1 through 10!
}
```

**Why this happens:** `i++` is postfix — the comparison uses `i`'s value *before* incrementing, but the increment has *already happened* by the time control reaches the loop body. So the printed value is always one step ahead of what was actually compared.

```java
// RIGHT (fix #1): use < instead of <=, compensating for the extra increment
int i = 1;
while (i++ < 10) {
    System.out.println(i);   // now correctly prints 1 through 10
}

// RIGHT (fix #2, clearer): keep the increment as a separate statement, avoid folding it into the condition
int i = 1;
while (i <= 10) {
    System.out.println(i);
    i++;
}
```

> **Interview-gold line:** *"Folding a postfix increment into a loop condition is a classic off-by-one bug source — the comparison happens against the pre-increment value, but by the time the loop body runs, the variable has already moved past it."* Prefer keeping increment/decrement as an explicit, separate statement for readability, even though Java allows folding it into the condition.



### 3. `do-while` Loop

#### Syntax

```java
do {
    // do something
} while (expression);
```

**Key difference from `while`: the body executes FIRST, and the condition is checked AFTERWARD** — meaning a `do-while` loop's body always runs **at least once**, regardless of whether the condition is true or false to begin with.

```java
int i = 1;
do {
    System.out.println(i);
    i++;
} while (i <= 10);
```

#### The critical distinguishing behavior

```java
// while loop: if the condition fails on the very first check, the body NEVER runs
int i = 11;
while (i <= 10) {
    System.out.println(i);   // NEVER executes — condition is false from the start
}

// do-while loop: the body runs ONCE regardless, THEN the condition is checked
int i = 11;
do {
    System.out.println(i);   // executes ONCE, printing 11
    i++;
} while (i <= 10);           // now checked — false, so loop exits after just one run
```

#### The canonical real-world use case: menu-driven selection

> **Why `do-while` is the natural fit for menu systems:** regardless of what the user will eventually choose, you must show the menu **at least once** before you can know their choice. The decision to loop again (or exit) can only be evaluated *after* that first interaction.

```java
// Conceptual menu structure
do {
    // 1. Show menu: "1. Play Game  2. Load Saved Game  3. Exit"
    // 2. Read user's choice
    // 3. Act on the choice (play game, load save, etc.)
} while (userChoice != EXIT);
```

`while` would require some artificial trick (like initializing a flag to `true` before the loop) to force the first display — `do-while` expresses this need directly, with no workaround required.



### 4. `for` Loop

The most commonly used loop in practice — packages three distinct pieces (initialization, condition, update) into one compact header.

#### Syntax

```java
for (initialization; condition; update) {
    // do something
}
```

```java
for (int i = 1; i <= 10; i++) {
    System.out.println(i);
}
```

- **Initialization**: runs exactly **once**, before the loop starts (`int i = 1`).
- **Condition**: checked before every iteration, same semantics as `while` (`i <= 10`).
- **Update**: runs at the end of every iteration, right before the condition is re-checked (`i++`).

#### Full execution trace

```
1. Initialization statement executes ONCE: i = 1
2. Condition evaluated: i <= 10 → true/false
3. If true → execute loop body
4. Loop body finishes → control returns to the for statement
5. Update statement evaluated: i++
6. Back to step 2 — repeat until condition is false
7. Condition false → exit loop entirely
```

#### Reverse direction — same idea as `while`

```java
for (int i = 10; i >= 1; i--) {
    System.out.println(i);
}
```



### 5. All Three `for` Loop Parts Are Optional

This is a fact many learners never realize — every one of the three sections in `for (...)` can be omitted (though the two semicolons are always required as placeholders).

```java
// Initialization done beforehand, outside the loop:
int i = 1;
for (; i <= 10; i++) { ... }

// Condition omitted entirely → runs forever unless something else (e.g., break) stops it:
for (int i = 1; ; i++) { System.out.println("hello"); }   // INFINITE LOOP

// Update omitted → i never changes → condition never becomes false → INFINITE LOOP:
for (int i = 1; i < 10; ) { System.out.println("hello"); }

// ALL THREE omitted:
for (;;) { System.out.println("hello"); }   // syntactically valid, unconditional infinite loop
```

> **Interview-gold line:** *"`for (;;)` is syntactically valid Java and compiles cleanly — it's an intentional idiom for an infinite loop, functionally identical to `while (true)`."*



### 6. Comma-Separated Variation in `for`

The `for` loop's initialization and update sections can each hold **multiple comma-separated statements**, letting you manage more than one loop variable simultaneously.

```java
for (int i = 1, j = 1; i <= 10; i++, j += 2) {
    System.out.println(i * j);
}
```

- `i` starts at 1, increments by 1 each time.
- `j` starts at 1, increments by 2 each time — a completely independent progression from `i`.
- The **condition slot does NOT support comma-separation** — only one boolean expression is allowed there. To combine multiple stopping conditions, use logical operators:

```java
// WRONG: comma inside the condition slot
for (int i = 1, j = 1; i <= 10, j <= 5; i++, j++) { ... }   // compile error: unexpected token ','

// RIGHT: combine conditions with a logical operator
for (int i = 1, j = 1; i <= 10 && j <= 5; i++, j++) { ... }
```

> With `&&`, the loop stops the moment **either** `i` or `j` exceeds its own bound — whichever happens first.

#### The condition doesn't have to be an increment-style comparison at all

Any `boolean`-evaluating expression works, including a flag variable toggled elsewhere inside the loop body:

```java
boolean keepGoing = true;
for (int i = 1; keepGoing; i++) {
    if (/* some condition */) {
        keepGoing = false;   // this will end the loop on the NEXT condition check
    }
}
```



### 7. Type Promotion Inside Loops — Why `int`, Not `byte`/`short`

You will almost never see a loop counter declared as `byte` or `short`, even when the iteration count is small enough to fit comfortably in either.

```java
// WRONG (technically compiles, but pointless): trying to "save space" with a narrower type
for (byte i = 1; i <= 10; i++) { ... }

// RIGHT (idiomatic Java): just use int
for (int i = 1; i <= 10; i++) { ... }
```

**Why the `byte` version gains nothing:** per Lecture 5's type promotion rules, `byte`/`short` operands are always promoted to `int` **during any calculation** — and every iteration involves at least one calculation (the comparison, the increment). So `i` effectively becomes an `int` internally on every single iteration regardless of its declared type — there's no actual memory savings, only the illusion of one. This is *why* idiomatic Java code almost universally declares loop counters as `int` (or `long` only when the iteration count genuinely could exceed `int`'s range).



### 8. Nested Loops

Just like nested `if`/`switch`, loops can contain other loops to any depth.

```java
for (int i = 1; i <= 5; i++) {
    for (int j = 1; j <= 5; j++) {
        // do something
    }
}
```

#### Execution model — the inner loop runs to completion for EVERY outer iteration

```
Outer i=1: inner j runs 1→5 completely
Outer i=2: inner j runs 1→5 completely (again, from scratch)
Outer i=3: inner j runs 1→5 completely
Outer i=4: inner j runs 1→5 completely
Outer i=5: inner j runs 1→5 completely
```

The inner loop's variable is **freshly re-initialized** on every single outer iteration — it does not "remember" where it left off.

#### Time complexity — O(n × m), commonly O(n²)

If the outer loop runs `n` times and the inner loop runs `m` times **for every** outer iteration, the total number of inner-body executions is `n × m`.

```
n = 5, m = 5  →  5 × 5 = 25 total inner executions
```

When `n == m` (a common case, as above), this is written as **O(n²)**. A third level of nesting (three loops, each running `n` times) gives **O(n³)**, and so on.

> **Interview-gold line:** *"Nested loops multiply their iteration counts — two nested loops each running n times give O(n²) total work, which is why deeply nested loops are a common performance red flag in code review, especially as n grows large."*



### 9. Pattern Printing with Nested Loops

A classic exercise for internalizing nested-loop mechanics: printing shapes made of characters.

#### Right-angle triangle of stars

```
*
* *
* * *
* * * *
* * * * *
```

```java
for (int i = 1; i <= 5; i++) {       // OUTER loop: controls which ROW we're on
    for (int j = 1; j <= i; j++) {   // INNER loop: controls how many stars THIS row gets
        System.out.print("* ");
    }
    System.out.println();            // after each row's stars are done, move to a new line
}
```

**The key design insight:** the inner loop's upper bound is **`i`, not a fixed number** — this is what makes row 1 print once, row 2 print twice, and so on. Using a fixed bound (e.g., `j <= 5` always) would print a full rectangle instead of a triangle.

```java
// WRONG: fixed inner bound produces a rectangle, not a triangle
for (int i = 1; i <= 5; i++) {
    for (int j = 1; j <= 5; j++) { ... }   // always 5 stars per row — not what we want
}

// RIGHT: inner bound tied to the outer loop's current value
for (int i = 1; i <= 5; i++) {
    for (int j = 1; j <= i; j++) { ... }   // row i gets i stars
}
```

**`print` vs `println` matters here:** use `print` (no line break) for repeated characters within a row, and a single bare `println()` (no argument) after the inner loop finishes, to move to the next row.



### 10. Jump Statements: `break` and `continue`

A **third category** of flow-of-control alteration (alongside Selection and Iteration): statements that abruptly redirect execution regardless of the surrounding loop's normal condition-checking.

```
Jump Statements
├── break     — exit the loop entirely, immediately
└── continue  — skip the rest of THIS iteration, jump straight to the next one
```

> `break` is not exclusive to `switch` (where it was first introduced in Lecture 7) — it works in any loop.

#### `break` — early exit

```java
for (int i = 1; i <= 10; i++) {
    if (i > 5) {
        break;   // exits the loop entirely, right now
    }
    System.out.println(i);
}
// Output: 1 2 3 4 5   (6 through 10 never print, and the loop stops entirely)
```

**Why not just change the loop's own condition instead (e.g., `i <= 5`)?** Because the exit decision might depend on something dynamically computed inside the loop body — not something you can express cleanly as the loop's own upper bound.

```java
boolean shouldStop = false;
for (int i = 1; i <= 10; i++) {
    // ... some logic that might set shouldStop = true dynamically ...
    if (shouldStop) {
        break;
    }
}
```

#### Practical example: an early-exit prime check

```java
int p = 9;
boolean isPrime = true;   // conceptual flag, simplifying the transcript's approach

for (int i = 2; i < p; i++) {
    if (p % i == 0) {
        System.out.println("The number is not prime");
        break;   // no need to keep checking further divisors once one is found
    }
}
```

**Why `break` matters for efficiency here:** the moment a single divisor is found, the number is conclusively *not* prime — continuing to check remaining candidates would be wasted work. `break` stops the loop the instant the answer is already determined.

> Note: this transcript's specific implementation checks up to `p - 1`; a genuinely optimized prime check only needs to go up to `√p`, but that refinement is a separate algorithmic topic, not a language-mechanics one.

#### `continue` — skip to the next iteration

```java
for (int i = 1; i <= 10; i++) {
    if (i % 2 == 0) {
        continue;   // skip printing for even numbers; jump straight to the next iteration
    }
    System.out.println(i);
}
// Output: 1 3 5 7 9   (odd numbers only)
```

**`continue` does NOT exit the loop** — it only skips whatever remains of the *current* iteration's body, then proceeds normally to the loop's update step and condition re-check.

#### `break` vs `continue` — the core distinction

| | Effect |
|||
| `break` | Exits the entire loop immediately — no further iterations, at all |
| `continue` | Skips the rest of the current iteration only — the loop keeps going |



### 11. `break`/`continue` Inside Nested Loops — Which Loop Do They Affect?

**Default rule: `break`/`continue` (without a label) always applies to the innermost enclosing loop** — the one whose braces directly contain the statement.

```java
for (int i = 1; i <= 10; i++) {          // OUTER
    for (int j = 1; j <= i; j++) {       // INNER
        if (j >= 5) {
            break;   // this breaks the INNER loop only — the outer loop keeps running
        }
        System.out.print("* ");
    }
    System.out.println();
}
```

**Effect:** once any row reaches 5 stars, that row's inner loop stops early (capped at 5 stars), but the outer loop continues to the next row as normal — producing a triangle that grows normally up to row 5, then flattens to a constant 5-star width for all subsequent rows, rather than continuing to grow.



### 12. Labels — Breaking/Continuing an OUTER Loop by Name

To make `break`/`continue` target a loop **other than the innermost one**, Java lets you attach a **label** (an identifier followed by a colon) directly before any loop (or even any plain code block):

```java
outer: for (int i = 1; i <= 10; i++) {
    inner: for (int j = 1; j <= i; j++) {
        if (j >= 5) {
            break outer;   // exits BOTH loops entirely — not just the inner one
        }
        System.out.print("* ");
    }
    System.out.println();
}
```

**Effect of `break outer`:** the moment the condition fires, execution jumps completely past the outer loop as well — the triangle simply **stops growing at row 5** and the program moves on to whatever comes after both loops, rather than flattening out for the remaining rows.

```java
// You can label continue too:
outer: for (int i = 1; i <= 10; i++) {
    inner: for (int j = 1; j <= i; j++) {
        if (someCondition) {
            continue outer;   // skips the rest of the INNER loop AND the rest of THIS outer iteration,
                               // jumping straight to the outer loop's next iteration
        }
    }
}
```

> **Interview-gold summary:** *"An unlabeled `break`/`continue` always targets the innermost enclosing loop. A labeled `break label;`/`continue label;` lets you target any enclosing loop by name — essential when a deeply nested loop structure needs to abort or skip an outer iteration based on inner-loop logic."*

#### Labels aren't exclusive to loops — plain code blocks can be labeled too

```java
first: {
    second: {
        third: {
            System.out.println("hello");
            break first;   // exits ALL THREE blocks at once, by name
        }
    }
}
```

A bare `{ }` block (no loop, no `if`, nothing) is itself a valid, labelable unit in Java. This has limited everyday use, but demonstrates that labels are a general control-flow feature, not something bolted on only for loops.



### 13. Variable Scope — A Preview

```java
for (int i = 2; i < p; i++) {
    // i is accessible HERE, inside the loop
}
System.out.println(i);   // WRONG: compile error — "i cannot be resolved to a variable"
```

**Why:** a variable declared inside a loop's parentheses (or any block delimited by `{ }`) is only accessible **within that block** — its **scope** ends at the closing brace. To use the loop counter after the loop finishes, declare it **outside** the loop first:

```java
int i;   // declared outside — scope now extends beyond the loop
for (i = 2; i < p; i++) {
    // ...
}
System.out.println(i);   // RIGHT — i is still in scope here
```

> Full treatment of variable scope is deferred to a dedicated later lecture — this is just the practical trigger for why it matters here.



### Quick Self-Check

> **Q1.** Why does `while (i++ <= 10)` produce different output than `while (i <= 10) { ...; i++; }`?

*Answer:* `i++` is postfix — the comparison uses the pre-increment value, but the increment has already happened by the time the loop body executes, shifting all printed values one step ahead of what was actually compared.

> **Q2.** When would you choose `do-while` over `while`?

*Answer:* When the loop body must execute at least once regardless of the condition — e.g., displaying a menu before the user has had any chance to make a choice that the loop condition depends on.

> **Q3.** Why is it pointless to declare a `for` loop's counter as `byte` instead of `int`, even for a small iteration count?

*Answer:* Because `byte`/`short` are always promoted to `int` during any calculation (per Lecture 5's type promotion rules), and every loop iteration involves calculations (the comparison and the increment) — so the counter is effectively an `int` internally regardless of its declared type, with no real memory savings.

> **Q4.** In a nested loop, what does an unlabeled `break` inside the inner loop actually stop?

*Answer:* Only the innermost enclosing loop — the outer loop continues running normally. To stop the outer loop too, you need a labeled `break outerLabel;`.



### Golden Rules / Checklist

- [ ] **`while`**: condition checked *before* each iteration — may run zero times if false from the start.
- [ ] **`do-while`**: body runs *first*, condition checked *after* — always runs at least once. Best fit for menu-driven / "show first, decide after" scenarios.
- [ ] **`for`**: packages initialization (once), condition (every iteration, before the body), and update (every iteration, after the body) into one header.
- [ ] Every loop needs a genuine path to making its condition false — forgetting the update step (or folding it incorrectly into the condition) causes infinite loops or off-by-one errors.
- [ ] All three `for` header sections are optional (`for (;;)` is valid, unconditional infinite loop) — but the two semicolons are always required as placeholders.
- [ ] `for`'s init/update sections support comma-separated multiple statements; the **condition slot does not** — combine multiple stopping conditions with `&&`/`||` instead.
- [ ] Loop counters are idiomatically declared `int`, never `byte`/`short` — type promotion makes the narrower types pointless inside any loop.
- [ ] Nested loops: the inner loop runs to completion for **every single** outer iteration; total work is roughly `outer-count × inner-count`, commonly written **O(n²)** for two equally-sized nested loops.
- [ ] **`break`** exits the (innermost, by default) loop entirely and immediately. **`continue`** skips only the rest of the current iteration, then proceeds to the next one normally.
- [ ] **Labels** (`label: for (...) {...}`) let `break label;`/`continue label;` target a *specific* enclosing loop (or even a plain code block) rather than just the innermost one.
- [ ] A variable declared inside a loop's parentheses/body is scoped to that loop — accessing it afterward requires declaring it outside the loop first.



### Practice Questions

**Basic**
1. Write a `for` loop that prints the numbers 10 down to 1.
2. What is the key behavioral difference between `while` and `do-while`?
3. What does `continue` do differently from `break`?

**Intermediate**
4. Explain why `for (int i = 1; i <= 10; i++);` (note the trailing semicolon) followed by an indented `System.out.println(i);` prints only once, if at all, and what the actual loop body is in this code.
5. Write a nested loop that prints a 5×5 grid of `*` characters, then modify it to print only a right-angle triangle.
6. A junior developer declares `for (short i = 0; i < 1000; i++)` to "save memory." Explain why this reasoning is flawed.
7. Why does `for (int i = 1, j = 10; i <= 5 && j >= 1; i++, j--)` work, but `for (int i = 1, j = 10; i <= 5, j >= 1; i++, j--)` does not?

**Advanced / Interview-style**
8. In a triple-nested loop where you want to abort ALL THREE loops the moment a condition is met in the innermost one, write the labeled break statement(s) needed, and explain why an unlabeled `break` would be insufficient.
9. Explain, using the concept of scope, why the following fails to compile, and how you'd fix it:
   ```java
   for (int i = 0; i < 10; i++) { }
   System.out.println(i);
   ```
10. A candidate says "do-while and while loops are functionally interchangeable if you just check the condition once beforehand." Under what circumstance does this claim break down, and why does that scenario matter for real-world code (e.g., reading user input)?
11. Explain why nested loops multiply their complexity rather than add it — walk through the total iteration count for an outer loop of size `n` and an inner loop of size `m` that runs fully for every outer iteration.


#### Classes & Objects

#### Class vs. Object

A **class** is a blueprint/template — it defines what fields and methods something will have, but it doesn't exist as real data yet. `Student` is a class: it says "every student will have a `name`, `age`, `rollNo`, and a `print()` method" — but no actual student exists just from writing the class.

An **object** is an actual instance created from that blueprint, sitting in memory with real values. `s1 = new Student("Ritesh Kaushal", 28, 01)` is an object — a real, specific student with actual data filled in.

**Analogy:** `Student` is like the blueprint for a house. `s1` and `s2` are two actual houses built from that same blueprint — same structure, different addresses (different data).



#### What happens in memory with `Student s1 = new Student();`

Two separate things happen:
1. `new Student()` creates the **actual object** in the **heap** (a memory region for dynamically created objects).
2. `s1` is a **reference variable**, stored separately (typically on the stack for local variables), which holds the **address/pointer** to that object in the heap — not the object itself.

So `s1` doesn't *contain* the Student — it *points to* where the Student lives. This is exactly the same relationship you already learned with arrays being stored on the heap and the variable being a reference to it.

```
Stack:            Heap:
s1 ──────────►   [Student object: name, age, rollNo]
```



####  Do two objects share memory or get separate copies?

Each object gets its **own separate copy** of the instance fields. When you did `s1 = new Student(...)` and `s2 = new Student(...)`, Java allocated **two distinct blocks of memory** on the heap — changing `s1.name` has zero effect on `s2.name`. They're independent, even though both came from the same class blueprint.

(Exception you'll learn later: `static` fields **are** shared across all objects of a class — but normal instance fields, like the ones you've been using, are always per-object.)


####  Does every class need a `main` method?

No. **Only one entry point is needed per program** — the `main` method is where Java starts executing. You've already proven this yourself: your `Employee`, `Movie`, `Circle`, `Product`, `Rectangle`, `BankAccount` classes have **no `main` method at all** — only your outer class (`DEMO2`) has it. A Java program can have many classes; only one needs `main` (and even that one only needs it if you're running that specific class directly).



####  Empty class, can you still create an object? What does printing it show?

Yes — you can create an object of a completely empty class (no fields, no methods):

```java
class Empty {}

Empty e = new Empty();
System.out.println(e);
```

This still compiles and runs. `System.out.println(e)` won't error — it'll print something like:

```
Empty@1b6d3586
```

That's the class name, followed by `@`, followed by the object's **hash code** (a memory-related identifier) in hexadecimal. This is Java's **default `toString()` behavior** — every object has this unless you override it. This is the same category of output you saw earlier when you tried `println(copyArray)` directly — arrays and plain objects both default to this format unless told otherwise.



####  Real-world non-physical object example

A **BankAccount** you already built is actually a perfect example — you can't physically touch "a bank account," but it has real state (`accountHolder`, `balance`) and behavior (`showBalance()`).

Another common one: an **Order** in an e-commerce system (like Flipkart) — fields: `orderId`, `items`, `totalAmount`, `status`; behaviors: `calculateTotal()`, `cancelOrder()`, `trackStatus()`. Nothing physical about "an order" — it's a concept, but modeling it as a class/object lets code represent and manipulate it just like a physical thing.



#### Golden Rules — Classes & Objects (full topic recap)

- ✅ Class = blueprint, Object = actual instance with real data in memory.
- ✅ Objects live on the **heap**; the variable holding them is just a **reference/pointer**, not the object itself.
- ✅ Each object has its own independent copy of instance fields — no sharing, unless a field is explicitly `static`.
- ✅ Only one class in a program needs a `main` method — it's the entry point, not a requirement for every class.
- ✅ Printing an object directly without a custom `toString()` shows `ClassName@hashcode` — not the field values.
- ✅ Objects can model non-physical, conceptual things just as easily as physical ones (`BankAccount`, `Order`, `Employee` are all valid, even though you can't hold them in your hand).

#### Constructors

####  Default values of primitive types and object references

Every field, if not explicitly assigned, gets a default value **automatically** (this only applies to instance/class fields — local variables inside methods do **not** get default values and will cause a compile error if used unassigned).

| Type | Default value |
|||
| `int` | `0` |
| `double` | `0.0` |
| `boolean` | `false` |
| `String` (or any object reference type) | `null` |

`null` means "this reference points to nothing" — it's not an empty string `""`, it's the complete absence of an object. This is why in Q1/Q2 of your practice set, printing an unassigned `String` field prints the literal word `null`, while an unassigned `int` prints `0`.



####  Does Java still give you a default constructor if you write a parameterized one?

**No.** Java only auto-generates a no-argument default constructor if you write **zero constructors yourself**. The moment you write even one constructor — parameterized or not — Java assumes you're taking full control of object creation, and it stops providing the free default one.

```java
class Student {
    Student(String name) { }   // you wrote this
}

Student s = new Student();   // COMPILE ERROR — no matching constructor
```

This is a very common beginner trap — code that worked fine before adding a constructor suddenly breaks elsewhere in the program because the free no-arg constructor silently disappeared.



####  Can a constructor have a return type like `void`?

**No — a constructor cannot have any return type, not even `void`.** This is actually the exact syntax rule that distinguishes a constructor from a regular method:

```java
Student() { }        // constructor — no return type at all
void Student() { }   // this is NOT a constructor — it's a regular method that happens to share the class name
```

The moment you add **any** return type (including `void`), Java treats it as an ordinary method, not a constructor — and it won't run automatically when you do `new Student()`.

**Rule:** constructor name = exact class name, **zero return type**, not even `void`.



#### Can you call a constructor manually like `obj.Student()`?

**No.** Constructors can only be invoked via the `new` keyword (`new Student()`), or from **inside another constructor of the same class** using `this(...)`, or from a subclass using `super(...)` (you'll hit this in inheritance later). You cannot call a constructor on an already-existing object like a regular method (`obj.Student()`), because a constructor's entire job is to **create and initialize** an object — it doesn't make sense to run it again on something that already exists.

If you literally write `obj.Student()`, Java will look for a **method** named `Student` — and since none exists, it's a compile error.



####  Why use constructor chaining (`this(...)`) instead of duplicating code?

Without chaining, if you have multiple constructors, you'd repeat the same field-assignment logic in every single one:

```java
// WITHOUT chaining — duplicated logic
Account(String holder, double balance) {
    this.accountHolder = holder;
    this.balance = balance;
    this.accountType = "Savings";
}

Account(String holder, double balance, String type) {
    this.accountHolder = holder;
    this.balance = balance;
    this.accountType = type;
}
```

If you ever need to change how initialization works (e.g., add validation, logging, a new default), you'd have to update it in **every** constructor separately — easy to forget one and introduce bugs.

```java
// WITH chaining — single source of truth
Account(String holder, double balance) {
    this(holder, balance, "Savings");   // delegates to the other constructor
}

Account(String holder, double balance, String type) {
    this.accountHolder = holder;
    this.balance = balance;
    this.accountType = type;
}
```

Now there's only **one** place where fields are actually assigned — every other constructor just calls into it with different defaults. This is the same principle as avoiding copy-pasted code anywhere else — one source of truth, less room for bugs.


####  Rules for `this(...)`: what line must it be on?

`this(...)` **must be the very first statement** inside the constructor — no exceptions, and you cannot put any code before it (not even a `System.out.println` for debugging).

```java
// WRONG — compile error
Account(String holder, double balance) {
    System.out.println("Creating account");
    this(holder, balance, "Savings");   // ERROR — this() must be first
}
```

```java
// RIGHT
Account(String holder, double balance) {
    this(holder, balance, "Savings");
    System.out.println("Creating account");   // fine here, after this()
}
```

**Why:** Java needs to guarantee that object initialization (via the chained constructor) fully completes *before* any other code in the current constructor runs — otherwise fields could be accessed or used before they're actually set up, leading to inconsistent object state.



## Golden Rules — Constructors (full recap)

- ✅ Unassigned fields get automatic defaults: `0` for numbers, `false` for `boolean`, `null` for any object type — but **local variables never get defaults**, they must be explicitly assigned before use.
- ✅ Writing even one constructor removes Java's free default no-arg constructor — if you still need `new ClassName()` with no args, you must write it yourself.
- ✅ A constructor has **zero return type** — not even `void`. Adding a return type turns it into a regular method with the same name, not a constructor.
- ✅ Constructors can only run via `new`, or via `this(...)`/`super(...)` from within another constructor — never as a regular method call on an existing object.
- ✅ `this(...)` chaining avoids duplicating field-assignment logic across multiple constructors — one constructor does the real work, others just delegate with different defaults.
- ✅ `this(...)` must always be the **first line** in a constructor — nothing can execute before it.

Try Q1–Q8 (the coding ones) now with all this in mind — the chaining questions (7, 8) will make a lot more sense with Q13/Q14 fresh.
