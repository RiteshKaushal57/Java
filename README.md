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
|-|-|-|
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


### 5. Type Conversion & Type Promotion


#### 1. Two Categories of Type Conversion

```
Type Conversion
├── Implicit  — compiler does it automatically (no code needed from you)
└── Explicit  — you must tell the compiler what to do, via casting
```



#### 2. Implicit Conversion — "Widening Conversion"

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
|-|-|-|-|
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


## 9. Arrays & Introduction to Strings


### 1. Why Arrays Exist

Without arrays, storing multiple related values means declaring a separate variable for each:

```java
// WRONG (does not scale): a new variable per value
int rollNumber1 = 1001;
int rollNumber2 = 102;
int rollNumber3 = 103;
// ...repeated for every student — unmanageable beyond a handful of values
```

**The core idea:** instead of scattering individually-named variables across memory, allocate one large **contiguous** block of memory upfront, split into equal-sized sections, and give the whole block a single name.

```
Contiguous memory: [ section 0 | section 1 | section 2 | section 3 | ... ]
                      ↑ one after another, no gaps, single allocation
```

This is exactly what an **array** is.



### 2. What Is an Array

> **An array is a collection of a single, particular data type.**

- Every element in the array shares the same declared type (e.g., all `int`, all `char`).
- The array is stored as **one contiguous memory allocation** — not scattered individual containers.
- The compiler needs to know the element type **up front**, because it determines how much memory each slot needs (e.g., `int` → 32 bits per slot) and therefore how much total memory to reserve for the whole array.



### 3. Declaring and Defining a 1D Array

### Declaration

```java
int[] rollNumbers;
```

- `int` — the data type every element will hold.
- `[]` — signals to the compiler "this is an array, not a plain variable."
- `rollNumbers` — the array's identifier (name).

#### Definition (actually allocating the memory)

```java
rollNumbers = new int[3];
```

- **`new`** — a special keyword that allocates memory **in the heap** (heap memory internals are deferred to a later lecture on objects/classes — for now, treat it as a required keyword for creating an array).
- `int[3]` — repeats the data type, and specifies the **size** (number of elements) the array will hold.

#### Combined (the common idiom)

```java
int[] rollNumbers = new int[3];
```

**What happens internally:** the compiler allocates 3 contiguous 32-bit containers (since the type is `int`), reserves them under one collective name (`rollNumbers`), and prepares them to be filled.

```
rollNumbers → [ 32 bits | 32 bits | 32 bits ]   (contiguous — addresses are consecutive)
                 idx 0      idx 1      idx 2
```

> If the array's memory address for index 0 is, say, `1001`, index 1 won't be `1002` — it'll be `1001 + 32 bits worth of address space` (since each `int` slot occupies 32 bits) — but conceptually, they are placed back-to-back with no gaps.



### 4. Indexing

**Index** = the number identifying a specific slot's position within the array.

> **Java array indexing always starts at 0, not 1.** The first element is index `0`, the second is index `1`, and so on.

```java
rollNumbers[0] = 1001;   // store 1001 at the 0th (first) slot
rollNumbers[1] = 102;    // store 102 at the 1st (second) slot
rollNumbers[2] = 103;    // store 103 at the 1st... third slot
```

Retrieving a value uses the same square-bracket syntax:

```java
System.out.println(rollNumbers[0]);   // prints 1001
System.out.println(rollNumbers[1]);   // prints 102
System.out.println(rollNumbers[2]);   // prints 103
```


### 5. Using Loops to Populate & Print Arrays

Manually indexing every element (as above) defeats the purpose of using an array in the first place for anything beyond a handful of elements. **Loops are the standard way arrays are actually filled and read** in real code.

```java
int[] rollNumbers = new int[3];
int x = 1001;
for (int i = 0; i < 3; i++) {
    rollNumbers[i] = x;
    x++;
}
```

```java
for (int i = 0; i < 3; i++) {
    System.out.println(rollNumbers[i]);
}
```

> **Interview-gold line:** *"Loops and arrays are almost always paired in real code — arrays give you contiguous, indexable storage, and loops give you a way to traverse that storage without hardcoding an access line per element."*



### 6. `.length` — Getting an Array's Size Dynamically

```java
System.out.println(rollNumbers.length);   // prints 3
```

`arrayName.length` (no parentheses — it's a property, not a method call) returns the number of elements the array holds.

#### Why this matters in loops

```java
// WRONG (fragile): hardcoding the size
for (int i = 0; i < 3; i++) { ... }   // breaks silently if the array's actual size ever changes

// RIGHT: derive the bound from the array itself
for (int i = 0; i < rollNumbers.length; i++) { ... }
```

> **Interview-gold line:** *"Hardcoding an array's size inside a loop condition is a maintenance trap — if the array's size changes elsewhere in the code, the hardcoded loop silently becomes wrong (either missing elements or throwing an exception). `.length` keeps the loop bound always in sync with the actual array."*


### 7. `ArrayIndexOutOfBoundsException`

Accessing an index that doesn't exist within the array's bounds throws a runtime **exception**.

```java
int[] rollNumbers = new int[3];   // valid indices: 0, 1, 2
rollNumbers[3] = 100;             // WRONG: throws ArrayIndexOutOfBoundsException — index 3 doesn't exist
```

> **What an exception is, conceptually:** Java's way of signaling that something went wrong while running your program — here specifically, that you tried to access a memory slot outside the array's actual allocated bounds. (Full treatment of exceptions — types, handling — is deferred to a dedicated later lecture.)



### 8. Multi-Dimensional Arrays — 2D Arrays

#### The motivating problem

A 1D array works when each entity needs exactly **one** value (e.g., one roll number per student). It breaks down when each entity needs **multiple** related values — e.g., each student has marks in **three different subjects**.

```
        Hindi   English   CS
Student1  50      30      90
Student2  60      40      80
Student3  70      50      70
```

#### The core idea: "array of arrays"

> **A 2D array is an array whose every element is itself an array.**

```
1D array  = array of int          (each slot holds one integer)
2D array  = array of int[]        (each slot holds an entire array of integers)
```

#### Declaration and definition

```java
int[][] marks = new int[3][3];
```

- Two square-bracket pairs signal a 2D array.
- First `3` → number of **rows**.
- Second `3` → number of **columns** (elements per row).

#### Filling values by index

```java
marks[0][0] = 50;   // row 0, column 0
marks[0][1] = 30;   // row 0, column 1
marks[0][2] = 90;   // row 0, column 2
marks[1][0] = 60;
marks[1][1] = 40;
marks[1][2] = 80;
marks[2][0] = 70;
marks[2][1] = 50;
marks[2][2] = 70;
```

#### Printing with nested loops

```java
for (int row = 0; row < 3; row++) {
    for (int col = 0; col < 3; col++) {
        System.out.print(marks[row][col] + " ");
    }
    System.out.println();   // move to a new line after each row
}
```

**Outer loop** traverses rows; **inner loop** traverses the columns within the current row — the exact same pattern used for pattern-printing (Lecture 8).

#### Dynamic bounds with `.length` on a 2D array

```java
for (int row = 0; row < marks.length; row++) {
    for (int col = 0; col < marks[row].length; col++) {
        System.out.print(marks[row][col] + " ");
    }
    System.out.println();
}
```

- `marks.length` → number of **rows** (the outer array's size).
- `marks[row].length` → number of **columns in THAT specific row** (the size of the inner array at that row's index) — this distinction matters critically once rows can have different lengths (see §10).



### 9. Conceptual vs. Logical (Actual) Representation — The Critical Mental Model Shift

This is the single most important insight of this lecture.

> **The mental "grid/matrix/table" picture of a 2D array is a human convenience — it is NOT how Java actually stores it internally.**

#### Conceptual view (how humans visualize it)

```
      C0  C1  C2
R0 [  50  30  90 ]
R1 [  60  40  80 ]
R2 [  70  50  70 ]
```

Rows and columns, like a spreadsheet or a math matrix. This is how we draw it, think about it, and reason about it — but **the compiler has no concept of "rows" or "columns" at all.**

#### Logical/actual view (how the compiler & JVM actually store it)

The compiler only understands: *"allocate a contiguous block, and if an element is itself an array, point that slot at a separate array elsewhere."*

```java
int[][] marks = new int[3][3];
```

Internally:
1. Allocate an outer array of size 3 (named `marks`), where **each of its 3 slots will hold a reference to another array** (not a raw `int` directly).
2. `marks[0]` — a reference to its own separate int array (size 3).
3. `marks[1]` — a reference to a *different* separate int array (size 3).
4. `marks[2]` — a reference to yet another separate int array (size 3).

```
marks:        [ ref→arrA | ref→arrB | ref→arrC ]
                    │           │           │
arrA (marks[0]):  [ 50, 30, 90 ]
arrB (marks[1]):  [ 60, 40, 80 ]
arrC (marks[2]):  [ 70, 50, 70 ]
```

- `marks[0]` is itself a full-fledged array — accessible on its own, with its own `.length`.
- `marks[0][1]` means: *"go to the array `marks` points to at index 0 (call it `marks[0]`), then go to index 1 within THAT array."*

```java
System.out.println(marks[0].length);   // prints 3 — marks[0] IS an array in its own right
System.out.println(marks.length);      // prints 3 — the outer array's size (number of rows)
```

> **Interview-gold line:** *"A 2D array in Java isn't a true matrix with contiguous rows-and-columns memory — it's an outer array whose elements are references to separate inner arrays. This is exactly why Java allows 'jagged' arrays where each row has a different length — because each row is genuinely an independent array object, not a slice of one giant uniform block."* (References/objects are covered fully in a later OOP lecture — for now, treat this as: each row is its own separately-allocated array.)



### 10. Jagged Arrays — Rows With Different Lengths

Because each row is a genuinely independent array (not a fixed-width slice of one block), **rows can have different lengths from each other.**

#### Real-world motivation

Different students might take different numbers of subjects — Student 1 has 2 subjects, Student 2 has 3, Student 3 has 4. A uniform 3×3 grid can't represent this; a **jagged array** can.

#### Declaring a jagged array (rows defined separately)

```java
int[][] marks = new int[3][];    // 3 rows declared; column count deferred
marks[0] = new int[2];           // row 0 has 2 elements
marks[1] = new int[3];           // row 1 has 3 elements
marks[2] = new int[4];           // row 2 has 4 elements

marks[0][0] = 23; marks[0][1] = 25;
marks[1][0] = 34; marks[1][1] = 11; marks[1][2] = 90;
marks[2][0] = 56; marks[2][1] = 23; marks[2][2] = 78; marks[2][3] = /* ... */;
```

> Specifying the **column count is optional at declaration time** — `new int[3][]` is valid (3 rows, each row's own array assigned later). Specifying the **row count is mandatory** — you cannot write `new int[][]` with neither dimension specified.

#### Why the `.length` distinction from §8 matters here

```java
for (int row = 0; row < marks.length; row++) {           // 3 rows total
    for (int col = 0; col < marks[row].length; col++) {  // THIS row's own length — varies per row!
        System.out.print(marks[row][col] + " ");
    }
    System.out.println();
}
```

Using a single fixed bound (e.g., `marks[0].length` for every row) would be **wrong** the moment rows have different sizes — `marks[row].length` is what correctly adapts to each row's actual, independent length.


### 11. 3D (and Higher) Dimensional Arrays

The same "array of arrays" idea extends indefinitely: a 3D array is an **array of 2D arrays**.

```java
int[][][] arr = new int[3][3][3];
```

**Logical breakdown:**
1. Outer array, size 3, named `arr` — each slot points to a 2D array.
2. `arr[0]`, `arr[1]`, `arr[2]` — each is itself a 2D array (array of arrays), each sized 3×3.
3. `arr[0][0]`, `arr[0][1]`, `arr[0][2]` — each is itself a 1D array of size 3.
4. `arr[0][0][0]`, etc. — finally, actual `int` values.

**Conceptual visualization:** stack multiple 2D matrices (layers) on top of each other, like stacking flat squares to form a cube — rows, columns, and a third dimension often called "depth."

```
Layer 1 (2D matrix) ─┐
Layer 2 (2D matrix) ─┼─→ stacked = a 3D structure (a "cube")
Layer 3 (2D matrix) ─┘
```

> 3D arrays are uncommon in everyday code; going beyond 3D is rarer still, since the mental model becomes hard to reason about. But the underlying mechanism (array-of-arrays, recursively) scales to any number of dimensions Java allows.


### 12. Alternative Array Declaration Syntax

#### Square brackets can attach to either the type or the name

```java
int[] rollNumbers = new int[3];   // brackets after the type — Java's preferred idiom
int rollNumbers[] = new int[3];   // brackets after the name — also valid
```

The second form exists for **legacy compatibility with C/C++** style array declarations. Both compile identically; Java's own style convention favors the first.

#### Array literal initialization (skip `new`, provide values directly)

When you already know every value up front, you can skip the `new` keyword and size entirely:

```java
// WRONG (unnecessarily verbose when values are already known): 
int[] rollNumbers = new int[3];
rollNumbers[0] = 4; rollNumbers[1] = 5; rollNumbers[2] = 6;

// RIGHT (more concise): array literal
int[] rollNumbers = {4, 5, 6};
```

The compiler infers both the size (3) and the type from the literal values themselves.

#### The same idiom for 2D arrays

```java
int[][] marks = {
    {4, 5, 6},
    {7, 8, 9},
    {23, 34, 93}
};
```

Each inner `{...}` becomes one row. This also naturally supports jagged rows:

```java
int[][] marks = {
    {12, 14, 56},
    {34, 45, 67},
    {45, 67, 78}
};
```

### 13. Introduction to Strings (Preview — Full Depth Comes After OOP)

#### Where `String` fits in Java's type system

```
Data Types
├── Primitive       (byte, short, int, long, float, double, char, boolean)
└── Non-Primitive
    ├── Arrays       (just covered)
    ├── String       (this preview)
    └── Objects      (covered after OOP fundamentals)
```

`String` is a **non-primitive** type — full understanding of *why* (and its internals) is deferred until after Object-Oriented Programming is covered, since `String` is itself implemented as a class/object in Java.

#### Basic declaration

```java
String firstName = "Aditya";
String lastName = "Tandon";
```

- Data type: `String` (capital `S` — it's a class name, not a keyword like `int`).
- String literals are always wrapped in **double quotes** (`"..."`) — contrast with `char` literals, which use **single quotes** (`'a'`).

#### String concatenation with `+`

Inside a `String` context, `+` does **not** mean numeric addition — it means **concatenation** (joining two strings end-to-end).

```java
System.out.println(firstName + lastName);          // "AdityaTandon" — no space, since none was added
System.out.println(firstName + " " + lastName);     // "Aditya Tandon" — a literal space string joins them
```

```java
// WRONG (if a space is intended but omitted): 
System.out.println(firstName + lastName);   // "AdityaTandon"

// RIGHT: explicitly concatenate a space literal
System.out.println(firstName + " " + lastName);   // "Aditya Tandon"
```

> A single space `" "` (or even an empty string `""`) is itself a perfectly valid `String` — just a very short one. This becomes more relevant once String internals are covered in depth.

#### Storing a concatenation result

```java
String fullName = firstName + " " + lastName;
System.out.println(fullName);   // "Aditya Tandon"
```

Just like `int sum = a + b;` stores an arithmetic result, `String fullName = a + " " + b;` stores a concatenation result — the underlying expression/assignment mechanics are identical, just applied to a different type with different semantics for `+`.



#### Quick Self-Check

> **Q1.** Why does `int[] arr = new int[5];` followed by `arr[5] = 10;` throw an exception?

*Answer:* Valid indices for a size-5 array are 0 through 4 (Java arrays are zero-indexed). Index 5 is one past the last valid slot, triggering an `ArrayIndexOutOfBoundsException`.

> **Q2.** Why is `marks[row].length` used instead of a fixed number inside the inner loop of a 2D array traversal?

*Answer:* Because each row (`marks[row]`) is its own independent array with its own length — using a fixed number would be incorrect the moment rows have different sizes (a jagged array), and even in a uniform grid, `.length` keeps the code correct if the array's dimensions ever change.

> **Q3.** Why can Java support jagged 2D arrays (rows of different lengths) at all?

*Answer:* Because a 2D array isn't one giant contiguous rows×columns memory block — it's an outer array whose elements are references to separate, independently-sized inner arrays. Each row genuinely is its own array object, so nothing requires them to share a length.



#### Golden Rules / Checklist

- [ ] An **array** is a collection of a single data type, stored as one contiguous memory allocation — the element type must be known up front so the compiler knows how much memory to reserve per slot.
- [ ] Declaration (`int[] arr;`) reserves the name/type info; definition (`arr = new int[3];`) actually allocates the memory. `new` is required to allocate array memory (heap allocation — deferred detail).
- [ ] **Indexing always starts at 0** in Java — the last valid index is `length - 1`.
- [ ] Loops are the standard idiom for populating/reading arrays — avoid manually indexing every element by hand beyond trivial cases.
- [ ] `array.length` (a property, no parentheses) gives the current size — always prefer it over a hardcoded number in loop conditions.
- [ ] Accessing an index outside `0` to `length-1` throws `ArrayIndexOutOfBoundsException` at runtime.
- [ ] A **2D array is an array of arrays** — `int[][]` — first bracket = number of rows, second = number of columns.
- [ ] **The "grid/matrix" mental picture is conceptual only.** The actual (logical) storage is: an outer array whose elements are references to separate inner arrays — there is no single unified rows×columns memory block.
- [ ] Because each row is an independent array, Java supports **jagged arrays** — rows of differing lengths (`new int[3][]`, then assign each row's own array separately).
- [ ] When traversing a jagged 2D array, use `marks[row].length` for the inner bound, never a single shared column count.
- [ ] 3D+ arrays are arrays-of-2D-arrays (and so on) — same underlying "array of arrays" principle, recursively applied; rarely used beyond 2D/3D in practice.
- [ ] Array declarations can put `[]` after the type (preferred) or after the name (legacy C/C++-style, still valid); array literals (`{1, 2, 3}`) let you skip `new`/size when values are known upfront.
- [ ] `String` is a **non-primitive** type; literals use **double quotes**; `+` on strings means **concatenation**, not numeric addition — full String internals are deferred until after OOP.



#### Practice Questions

**Basic**   
1. Write the declaration and definition (as two separate lines) for an `int` array of size 5.   
2. What is the index of the last element in an array of length 10?    
3. What does `myArray.length` return, and is it a method call or a property access?    

**Intermediate**   
4. Explain why `int[][] grid = new int[4][4];` followed by `grid[4][0] = 1;` throws an exception, referencing the valid index range.   
5. Write a jagged 2D array representing 3 students with 2, 4, and 3 subjects respectively (values don't matter, just the structure), and write the nested loop that correctly prints it regardless of each row's length.   
6. Why does `firstName + lastName` (with no space) concatenate without any separator, and how would you fix it to include one?

**Advanced / Interview-style**   
7. Explain, using the "array of arrays" model, why a 2D array in Java can have jagged rows, but a true fixed-size matrix (as in some other languages with genuinely contiguous 2D memory) cannot.   
8. A colleague writes a nested loop using `marks[0].length` as the bound for every row's inner loop, assuming all rows are the same size. Under what circumstance does this silently produce wrong output rather than crashing, and under what circumstance does it crash?   
9. Explain the internal difference between `int[] arr = {1, 2, 3};` and `int[] arr = new int[3];` followed by manual assignment — do they produce the same runtime structure, just written differently, or is something fundamentally different happening?   
10. Why is `String` classified as a non-primitive type in Java, unlike `int` or `char`? (You may answer at the level of "it's a class/object" even without full internals — the goal is recognizing the categorical distinction.) 



## 10. Random Access, Memory Internals & Array of Strings


### 1. What "Random Access" Actually Means

> **Random access** = the ability to jump *directly* to any array index in constant time, without sequentially scanning from the start.

```java
int[] arr = new int[5];
System.out.println(arr[2]);   // JVM jumps STRAIGHT to index 2 — no traversal through 0, 1 first
```

This lecture answers: *how* does the JVM know exactly which memory address to jump to?



### 2. Prerequisite: Stack vs. Heap Memory

Recall the two data type categories:

```
Data Types
├── Primitive      (byte, short, int, long, float, double, char, boolean)
└── Non-Primitive  (arrays, String, objects — covered fully after OOP)
```

#### Primitives → Stack Memory

```java
int x = 4;
```

- A container is created **directly in stack memory**, named `x`, holding the value `4` itself.
- **"`x` directly holds the value 4."** This phrasing matters — it's the contrast point for what happens with non-primitives.

#### Non-Primitives (arrays) → Heap Memory + a Reference Variable

```java
int[] arr = new int[5];
```

- The actual array (5 contiguous `int`-sized slots) is allocated in a special memory region called the **heap**.
- The variable `arr` itself is created in **stack memory** — but it does **NOT** directly hold the array's contents.
- `arr` holds only an **address** — it **points to** where the array lives in the heap. This is called a **reference variable**.

```
STACK                          HEAP
┌─────────────┐                ┌───────────────────────┐
│ arr → [addr]│───points to──→ │ [ 0 | 1 | 2 | 3 | 4 ]  │  (the actual array)
└─────────────┘                └───────────────────────┘
```

> **Interview-gold line:** *"`int x = 4` means x directly holds 4. `int[] arr = new int[5]` means arr does NOT directly hold the array — it holds a reference (an address) pointing to the array's actual location in heap memory. This distinction — direct value vs. reference to a value — is the fundamental difference between primitive and non-primitive types in Java."*

> Full depth on stack/heap mechanics and references is deferred until objects/classes are covered — this lecture only goes as deep as needed to explain random access.



### 3. The Random Access Formula

#### Setup: how much space each element actually occupies

Since array memory is **contiguous**, and every element is the same fixed size (because it's the same data type), the address of any index can be computed directly — no need to walk through preceding elements.

```
address of arr[i] = baseAddress + (sizeOfDataType × i)
```

Where:
- **`baseAddress`** = the memory address where the array begins (index 0's location).
- **`sizeOfDataType`** = how many bytes each element occupies (4 for `int`, 8 for `long`/`double`, 1 for `boolean` in most JVMs, 2 for `char`, etc. — from Lecture 3).
- **`i`** = the index being accessed.

#### Worked example

```java
int[] arr = new int[5];   // base address, say, 100
arr[0] = 10; arr[1] = 20; arr[2] = 30; arr[3] = 40; arr[4] = 50;
```

Since `int` = 4 bytes, each slot occupies 4 bytes, so:

```
arr[0] → address 100 (100 + 4×0)
arr[1] → address 104 (100 + 4×1)
arr[2] → address 108 (100 + 4×2)
arr[3] → address 112 (100 + 4×3)
arr[4] → address 116 (100 + 4×4)
```

To fetch `arr[3]`:
```
address = 100 + (4 × 3) = 112
→ jump straight to address 112, read 4 bytes → get 40
```

**No loop, no sequential scan — one arithmetic calculation, then a direct jump.** This is the actual mechanism behind "random access."

> **Interview-gold line:** *"Random access isn't magic — it's a single arithmetic formula (`baseAddress + size × index`) the JVM computes to jump directly to the right memory offset, made possible specifically because array memory is contiguous and every element is a fixed, known size."*

### Where does the JVM get the base address from?

> **The reference variable itself IS the base address.**

```java
int[] arr = new int[5];   // 'arr' (in stack memory) stores the heap address where the array starts
```

`arr` doesn't hold the array's contents — it holds the number that *is* the base address (e.g., `100`). So `arr[i]` really means: *"take the address stored in `arr`, then apply the formula using that as `baseAddress`."*



### 4. Applying the Formula to Different Data Types

The formula is identical for every primitive array — only `sizeOfDataType` changes:

| Type | Size (bytes) | Formula |
|---|---|---|
| `byte` | 1 | `base + 1×i` |
| `short` | 2 | `base + 2×i` |
| `int` | 4 | `base + 4×i` |
| `long` | 8 | `base + 8×i` |
| `float` | 4 | `base + 4×i` |
| `double` | 8 | `base + 8×i` |
| `char` | 2 | `base + 2×i` |
| `boolean` | *see §5* | `base + size×i` |


### 5. The Special Case: `boolean`'s Size Is NOT Officially Fixed

Recall from Lecture 3: every other primitive has a hard-defined bit-width in the Java spec. `boolean` is different.

> **According to the official Java documentation, `boolean` has NO fixed size.** The JVM specification deliberately leaves this decision to each individual JVM implementation (HotSpot/Oracle, OpenJDK, etc.) to decide based on **that platform's own CPU optimization needs.**

#### Why a `boolean` could theoretically be just 1 bit

A `boolean` only ever needs to distinguish two states — `true`/`false` — which maps perfectly onto a single bit (`1` = true, `0` = false). In principle, 1 bit would be mathematically sufficient.

#### Why virtually every real JVM uses 1 full byte instead

> **The reason is CPU fetch granularity, not necessity.** CPUs fetch memory in byte-sized (or larger) chunks — never bit-by-bit. So even if a `boolean` conceptually only needs 1 bit, storing it as anything less than a full byte gains nothing, because the CPU would fetch a full byte's worth of memory regardless.

```
So for better CPU optimization, most JVMs (HotSpot, OpenJDK) settle on:
boolean size = 1 byte (8 bits)
```

> **Why didn't Java's spec just hardcode 1 byte itself, then?** Because the *optimal* fetch size can vary by platform/CPU architecture — by leaving it open, each JVM implementation can pick whatever size best matches its own target hardware's fetch behavior, rather than being locked into one number that might not be optimal everywhere.

> **Interview-gold line:** *"`boolean` has no JLS-mandated size specifically because it's a CPU-optimization decision, not a data-representation necessity — 1 bit is mathematically sufficient, but CPUs fetch in byte granularity, so JVMs universally round up to 1 byte for fetch efficiency, and the spec leaves this JVM-specific rather than hardcoding it."*

#### Applying the formula to a `boolean` array

```java
boolean[] arr = new boolean[5];
// arr[2] address = baseAddress + (1 × 2)
```

If base address is `100`: `arr[0]`→100, `arr[1]`→101, `arr[2]`→102, `arr[3]`→103, `arr[4]`→104 (1 byte apart, not 4).


### 6. `ArrayIndexOutOfBoundsException` — What Actually Happens Internally

Recall from Lecture 9 that accessing an out-of-bounds index throws this exception. Here's *why*, mechanically:

> **Before ever applying the address formula, Java's array implementation runs a bounds check.**

Conceptually:
```java
if (index < 0 || index >= array.length) {
    throw new ArrayIndexOutOfBoundsException(...);
}
// only if the check passes does the formula (baseAddress + size × index) actually run
```

**Why the check exists at all — safety, not laziness:** without it, `arr[100]` on a 5-element array would compute a "valid-looking" address using the formula (e.g., `100 + 4×100 = 500`) and blindly jump there — reading **whatever unrelated data happens to occupy that memory location**, which could belong to a completely different variable or structure entirely. The bounds check exists specifically to prevent the formula from ever being applied to a location outside the array's actual allocated space.

> **Interview-gold line:** *"The formula for random access is unconditional arithmetic — it would happily compute an address for any index, including invalid ones. The bounds check is what prevents that computed-but-invalid address from ever being dereferenced, protecting against reading memory that belongs to something else entirely."*



### 7. Random Access in 2D Arrays — Applying the Formula Twice

Recall from Lecture 9: a 2D array is really an **array of arrays** — the outer array holds **reference variables**, not raw values.

```java
int[][] arr = new int[3][4];
```

#### Step 1 — find the reference to the correct row

Each slot in the outer array holds a **reference** (an address), and — critically — **a reference variable itself occupies 4 bytes** (same as an `int`, in most common JVM implementations), regardless of what it points to.

```
address of arr[i] (a reference) = outerBaseAddress + (4 × i)
```

Reading those 4 bytes gives you **another address** — the base address of the specific row's own inner array.

#### Step 2 — apply the formula again, inside that row

```
address of arr[i][j] = (address stored at arr[i]) + (sizeOfDataType × j)
```

#### Fully worked example: finding `arr[1][2]`

```java
int[][] arr = new int[3][4];   // outer base address = 100
```

```
Step 1: address of arr[1] = 100 + (4 × 1) = 104
        Read 4 bytes at 104 → get a reference, say, 200 (this row's own base address)

Step 2: address of arr[1][2] = 200 + (4 × 2) = 208
        Read 4 bytes at 208 → get the actual int value
```

```
OUTER ARRAY (base 100):          ROW arr[1] (base 200):
[ ref→200 | ref→??? | ref→??? ]  [ 1 | 10 | 6 | 3 ]
    ↑ index 0  index 1  index 2      idx0 idx1 idx2 idx3
```

If row 1 held `[1, 10, 6, 3]`, then `arr[1][2]` reads index 2 of that row → `6`. Confirmed: two applications of the same base formula, one for the outer array's reference, one for the inner array's actual value.

> **Interview-gold line:** *"2D array access is the same random-access formula applied twice — first to fetch the reference to the correct row (since the outer array stores references, each occupying 4 bytes, just like an int), then to fetch the actual value from within that row's own array."*

#### This generalizes to any dimension

3D, 4D, etc. arrays just apply the same formula **once per dimension** — each level's "value" is a reference to the next level's array, until you reach the innermost array holding actual primitive values.



### 8. Array of Strings — Same Reference Mechanism

`String` is itself a **non-primitive** type — so an array of `String` works exactly like a 2D array's outer layer: it's an **array of references**, each pointing to a separately-allocated `String` somewhere else in the heap.

```java
String[] names = new String[3];
names[0] = "Aditya";
names[1] = "Abhay";
names[2] = "Rohit";
```

```
names (array of references, base address 400):
[ ref→100 | ref→200 | ref→300 ]
     ↓          ↓          ↓
  "Aditya"    "Abhay"    "Rohit"   (each String lives at its own separate heap address)
```

Fetching `names[1]`:
```
address of names[1] = 400 + (4 × 1) = 404
Read 4 bytes at 404 → get reference 200
Jump to address 200 → read the String "Abhay" stored there
```

#### Historical note: was `String` ever literally a `char[]`?

> **Up through JDK 8, `String` was internally implemented as a `char[]` (character array).** Since JDK 9, this changed internally (for memory optimization reasons involving how different character encodings are stored) — but this is an internal implementation detail, deferred to a dedicated String-internals lecture after OOP is covered.

```java
// Conceptually similar (pre-JDK 9 internal idea), NOT how you'd write it in modern code:
char[] name = {'A', 'd', 'i', 't', 'y', 'a'};   // fetching indices sequentially reconstructs "Aditya"
```

> **Interview-gold line, with appropriate hedging:** *"Conceptually, you can think of a String as being backed by a character array — and that was literally true internally up through JDK 8. Since JDK 9, the internal representation changed for memory efficiency, though the character-array mental model still helps for understanding indexed access like `charAt()`."*


### 9. The Real-World Payoff of Random Access: CPU Caching

> **Random access enables efficient CPU caching, which is the deeper performance reason arrays are so fast to traverse.**

CPUs fetch memory in byte-chunks (not necessarily one value at a time) into their own tiny, extremely fast internal memory called **registers/cache**. Because array elements are **contiguous**, fetching one element often pulls in *neighboring* elements "for free" — since the CPU fetched a wider byte-range than just the single requested value.

```
Fetching arr[3] (4 bytes) — but the CPU might fetch 8 bytes at once from that region,
which happens to ALSO include arr[4]'s bytes.

Result: if you access arr[4] right after arr[3], it's already sitting in the CPU's
cache — no need to go back to RAM at all.
```

> **Interview-gold line:** *"Because array memory is contiguous, accessing one element often brings neighboring elements into CPU cache as a side effect — this is why sequential array traversal is dramatically faster in practice than the same number of accesses to scattered, non-contiguous memory (like a linked list) would be, even though both are technically 'O(1) per access' in big-O terms."*


#### Quick Self-Check

> **Q1.** What is the difference between `int x = 4;` and `int[] arr = new int[5];` in terms of what the variable itself holds?

*Answer:* `x` directly holds the value `4` in stack memory. `arr` holds a **reference** (an address) in stack memory, pointing to the actual array, which lives separately in heap memory — `arr` does not directly hold the array's contents.

> **Q2.** Why doesn't Java define a fixed bit-width for `boolean` the way it does for `int` or `char`?

*Answer:* Because a `boolean` only needs 1 bit to represent true/false, but CPUs fetch memory in byte-sized chunks (not bits), so the "optimal" size is a CPU/platform-specific optimization decision, not a fixed data requirement — Java leaves it to each JVM implementation, and most settle on 1 byte.

> **Q3.** Walk through the formula for accessing `arr[2][1]` in a 2D `int` array with outer base address 500.

*Answer:* Step 1: address of `arr[2]` = 500 + (4×2) = 508 → read 4 bytes there to get the row's base address (say, 700). Step 2: address of `arr[2][1]` = 700 + (4×1) = 704 → read 4 bytes there to get the actual `int` value.

> **Q4.** Why does the JVM check bounds before applying the random-access formula, rather than just applying it and seeing what happens?

*Answer:* The formula itself is unconditional arithmetic — it will compute *some* address for any index, valid or not. Without a bounds check, an invalid index would compute an address outside the array's actual allocation and read whatever unrelated data happens to be there, silently corrupting behavior. The bounds check exists to catch this before it happens, throwing `ArrayIndexOutOfBoundsException` instead.



#### Golden Rules / Checklist

- [ ] **Primitives** are stored directly in **stack memory**, holding their value directly (`x` holds `4`).
- [ ] **Non-primitives (arrays, Strings, objects)** are stored in **heap memory**; the stack-memory variable is a **reference** holding only an address, pointing to the heap location.
- [ ] **Random access formula:** `address of arr[i] = baseAddress + (sizeOfDataType × i)` — this single calculation, not a scan, is what makes array access O(1).
- [ ] The reference variable itself **is** the base address used in the formula.
- [ ] `boolean` has **no JLS-fixed size** — it's a CPU-fetch-optimization decision left to each JVM; most (HotSpot, OpenJDK) use 1 byte, even though 1 bit is mathematically sufficient, because CPUs fetch in byte granularity.
- [ ] A **bounds check** (`index < 0 || index >= length`) always runs *before* the address formula — this is what prevents reading unrelated memory and is the actual mechanism behind `ArrayIndexOutOfBoundsException`.
- [ ] A **2D array** applies the random-access formula **twice**: once to fetch the reference to the correct row (references are typically 4 bytes, like an `int`), once more within that row to fetch the actual value.
- [ ] The same "array of references" model applies to **arrays of Strings** — each slot holds a reference to a separately-heap-allocated `String`.
- [ ] `String` was internally backed by a `char[]` through JDK 8; this changed internally in JDK 9+ for memory efficiency (full internals deferred to a later lecture).
- [ ] Random access's deeper real-world payoff is enabling **CPU caching** — contiguous memory means fetching one element often pulls neighboring elements into cache "for free," making sequential array traversal faster in practice than equivalent access patterns on non-contiguous structures.



#### Practice Questions

**Basic**   
1. What does a reference variable actually store — the data itself, or something else?
2. Write the random access formula in general form.   
3. Why is `boolean`'s size not fixed by the Java Language Specification?    

**Intermediate**   
4. Given a `long[]` array with base address 1000, compute the byte address of `arr[5]`.     
5. Explain, step by step, what "reading a reference" means when accessing `arr[i]` in a 2D array, before the second formula application even happens.    
6. Why does accessing an out-of-bounds array index not simply return garbage data, but instead throw an exception?   

**Advanced / Interview-style**   
7. Explain why array random access is considered O(1) even though, for a 2D or 3D array, the JVM technically performs multiple formula applications (one per dimension) rather than just one.    
8. A candidate claims "arrays are fast because of random access." Push back on this by explaining the *additional* CPU-caching benefit that contiguous memory provides beyond the O(1) access guarantee itself.    
9. Why can't the address-formula approach work for a data structure whose elements are NOT contiguous in memory (e.g., a linked list)? Tie your answer back to what specifically the random-access formula depends on.      
10. Explain why a reference variable pointing to a `String` takes a fixed, predictable number of bytes (e.g., 4) in the containing array, even though the `String` it points to can be arbitrarily long.

## 11. Functions, Overloading, Scope & Recursion

### 1. Why Functions Exist — The Reusability Problem

```java
// WRONG: repeating the same "add two numbers and print" logic every time you need it
int i = 4, j = 5;
int sum = i + j;
System.out.println(sum);

// ...later in the same program...
int k = 9, l = 10;
int sum2 = k + l;
System.out.println(sum2);

// ...and again, and again...
```

The same block of logic (declare two numbers, add, print) is duplicated everywhere it's needed. This is a **code reusability** problem.

> **A function is a named block of code that encapsulates a piece of logic you want to reuse.** Instead of rewriting the logic each time, you write it once, give it a name, and **call** it wherever needed.

Conceptually identical to a mathematical function `f(x)`: takes input(s), does some processing, and produces an output.


### 2. Anatomy of a Function

```java
static int sum(int a, int b) {
    int result = a + b;
    return result;
}
```

| Part | Example | Meaning |
|---|---|---|
| **`static`** | `static` | Required keyword for now — *why* is deferred until OOP. Treat as a black box. |
| **Return type** | `int` | The data type the function hands back to its caller. |
| **Function name** | `sum` | The identifier used to call it. |
| **Parameters** | `(int a, int b)` | The inputs the function expects — each declared as *type name* pairs. |
| **Body** | `{ ... }` | The logic executed when the function is called. |
| **`return` statement** | `return result;` | Hands the computed value back to whoever called the function. |

#### Return type = the type of the value actually returned

```java
static int sum(int a, int b) {
    int result = a + b;    // result is an int
    return result;         // returning an int → return type must be int
}
```

If the function returns nothing at all, the return type is `void` (see §5).

### 3. Calling a Function

```java
public static void main(String[] args) {
    int i = 4, j = 5;
    int result = sum(i, j);     // call sum, passing i and j
    System.out.println(result);  // prints 9
}
```

**What happens step by step:**
```
1. Execution reaches sum(i, j) — control JUMPS to the sum function.
2. i's value (4) is copied into parameter a; j's value (5) into parameter b.
3. The body runs: result = a + b = 9.
4. return result → 9 is handed back to the exact spot where sum(i, j) was called.
5. That returned value (9) gets assigned to the caller's variable 'result'.
```

You can pass literals directly instead of variables:
```java
int c = sum(10, 9);   // valid — 10 and 9 are passed straight in
```



### 4. Parameters vs. Arguments — The Terminology That Trips Up Interviews

```java
static int sum(int a, int b) { ... }    // 'a' and 'b' are PARAMETERS (placeholders in the definition)
sum(4, 5);                                // '4' and '5' are ARGUMENTS (actual values passed at call time)
```

> **Parameters** = the variable declarations in the function's *definition* (what the function *expects* to receive).
> **Arguments** = the actual values supplied when the function is *called*.

Parameter names don't need to match the caller's variable names — `sum(i, j)` passes `i`'s value into `a`, `j`'s into `b`; the names on each side are independent.


### 5. Four Types of Functions (by Input/Output)

Any function falls into exactly one of four categories:

| # | Takes Input? | Returns Output? | Example |
|---|---|---|---|
| 1 | No | No | `static void greet() { System.out.println("Hello"); }` |
| 2 | Yes | No | `static void sayHello(String name) { System.out.println("Hello " + name); }` |
| 3 | No | Yes | `static int getNumber() { return 10; }` |
| 4 | Yes | Yes | `static int multiply(int a, int b) { return a * b; }` |

#### `void` — the "returns nothing" return type

```java
static void greet() {
    System.out.println("Hello");
    // no return statement needed
}
```

- `void` tells the compiler "this function returns nothing."
- In a `void` function, a bare `return;` is **optional** — the function ends naturally when it reaches its closing brace.
- You **cannot** return a value from a `void` function.

#### Ignoring a return value

```java
getNumber();   // valid — the returned 10 is simply discarded, not stored anywhere
int x = getNumber();   // also valid — captures the returned value
```

You're never *forced* to capture a function's return value — it just gets lost if you don't. (This detail becomes important for overloading — see §8.)

#### Where do functions live?

Functions are defined **outside** `main`, but inside the class:

```java
public class Demo {
    public static void main(String[] args) {
        greet();                    // calling from main
    }

    static void greet() {           // defined OUTSIDE main, inside the class
        System.out.println("Hello");
    }
}
```



### 6. `main` Is Itself a Function

```java
public static void main(String[] args) {
    // your code
}
```

Decoding this now that you know function anatomy:

| Part | Meaning |
|---|---|
| `public` | Access modifier — deferred until OOP |
| `static` | Same placeholder keyword as any other function so far |
| `void` | Return type — `main` returns nothing |
| `main` | The function's name |
| `String[] args` | A single parameter: an array of `String`s |
| `{ ... }` | The function body |

> **`main` is the entry point of every Java program.** When you run a class, the JVM specifically searches for the function named `main` and begins execution there, line by line. Every other function you've defined is dormant — **it only runs if something calls it, ultimately traceable back to `main`.**

```java
public class Demo {
    static void greet() { System.out.println("Hello"); }   // defined but never called
    public static void main(String[] args) { }              // empty main
}
// Running this prints NOTHING — greet() is never called from main.
```

> **Interview-gold line:** *"`main` is just a function — the special part is that the JVM uses it as the entry point. Any other function only executes if it's called, directly or indirectly, from `main`."*

Also worth noting: the `return;` statement at the end of `main` is optional for the same reason it's optional in any other `void` function.


### 7. Function Calls Can Be Embedded Inside Expressions

Because a function that returns a value *is* a value at its call site, you can use the call directly wherever a value is expected:

```java
System.out.println(getNumber());      // getNumber() runs first, returns 10, which println then prints
System.out.println(multiply(2, 4));   // prints 8
int total = sum(3, 4) + 5;            // 7 + 5 = 12
```

### 8. Function Overloading

> **Function overloading** = defining multiple functions with the **same name** but **different parameter lists**, so one logical operation can accept different kinds of input.

```java
static int sum(int a, int b)           { return a + b; }          // 2 int params
static int sum(int a, int b, int c)    { return a + b + c; }      // 3 int params (different NUMBER)
static int sum(double a, double b)     { return (int)(a + b); }   // double params (different TYPE)
static void greet(String name, int age) { ... }                    // (String, int)
static void greet(int age, String name) { ... }                    // (int, String) — different ORDER
```

#### The three legal ways to differ

1. **Different number of parameters** (`sum(a,b)` vs. `sum(a,b,c)`)
2. **Different types of parameters** (`sum(int, int)` vs. `sum(double, double)`)
3. **Different order of parameter types** (`greet(String, int)` vs. `greet(int, String)`)

#### How the compiler picks which overload to call

By examining the **arguments** at the call site — their count, types, and order:

```java
sum(5, 6);          // 2 ints → calls sum(int, int)
sum(3, 5, 6);       // 3 ints → calls sum(int, int, int)
greet("Aditya", 28);  // (String, int) → calls the (String, int) version
greet(28, "Rohit");   // (int, String) → calls the (int, String) version
```

#### The critical restriction: return type alone is NOT enough

```java
// WRONG: compile error — "duplicate method fun() in type Demo"
static void fun() { System.out.println("Hello"); }
static int fun()  { System.out.println("Hello"); return 5; }
```

**Why this fails:** recall from §5 that a caller is free to **ignore a function's return value entirely**.

```java
fun();   // Which fun() did the programmer mean?
         // - the void one? Or the int one, with the returned 5 simply discarded?
```

Since the call site `fun();` looks identical whether or not the return value is being used, the compiler has no way to tell which version was intended based on the return type alone. To resolve this ambiguity, Java simply refuses to allow two functions that differ **only** in return type.

```java
// RIGHT: differ by at least one of number/type/order of parameters, OR rename
static void fun()                  { ... }
static int  fun(String name)       { ... }   // different parameter list → legal overload
```

> **Interview-gold line:** *"Return type is not part of a method's signature for overloading purposes — because a caller can always discard the return value, so the compiler can't use return type to disambiguate which overload the call site intended."*



### 9. Function Chaining (Nested Calls) & the Call Flow

A function can call another function, which can call another, and so on — a **chain**.

```java
public static void main(String[] args) {
    fun1();
    System.out.println("Bye");
}

static void fun1() {
    fun2();
    System.out.println("Hi");
}

static void fun2() {
    fun3();
    System.out.println("Hello");
}

static void fun3() {
    System.out.println("How are you?");
}
```

#### Execution trace — a call goes DEEP first, then unwinds back UP

```
main() calls fun1()
  fun1() calls fun2()
    fun2() calls fun3()
      fun3() prints "How are you?"     ← deepest point reached FIRST
      fun3() finishes → returns to fun2
    fun2() resumes → prints "Hello"
    fun2() finishes → returns to fun1
  fun1() resumes → prints "Hi"
  fun1() finishes → returns to main
main() resumes → prints "Bye"
```

**Output:**
```
How are you?
Hello
Hi
Bye
```

> **The key insight:** control always returns to **exactly where the function was called from** — the caller. The order of *printing* is the exact **reverse** of the order of *calling*, because the innermost function finishes first and each caller resumes only after its callee completes. (The underlying mechanism is the **call stack** — a stack-memory structure covered in depth in a later memory-management lecture.)


### 10. Scope of a Variable

> **Scope** = the region of code where a variable is accessible.

#### Local scope

A variable declared inside a **block** (delimited by `{ }`) exists only within that block.

```java
public static void main(String[] args) {
    int x = 4;
    int y = 5;
    System.out.println(x + " " + y);   // fine — x, y are in scope
    fun();
}

static void fun() {
    System.out.println(x);   // WRONG: compile error — "x cannot be resolved to a variable"
                              // x was declared in main; fun() has its own, separate scope
}
```

`x` and `y` belong to `main`'s local scope. `fun()` cannot see them — unless they're passed as arguments:

```java
public static void main(String[] args) {
    int x = 4, y = 5;
    fun(x, y);              // pass x and y as arguments
}

static void fun(int x, int y) {   // NEW, separate x and y (parameters), coincidentally same names
    System.out.println(x + " " + y);
}
```

Here, the `x`/`y` inside `fun` are **different variables** from `main`'s — they merely receive copies of the values.

#### Scope applies to every `{ }` block — not just functions

```java
if (x == 4) {
    int j = 7;       // j exists ONLY inside this if block
}
System.out.println(j);   // WRONG: compile error — j's scope ended at the closing brace
```

The same applies to `for`/`while` loop bodies and any bare `{ }` block.

#### Lifetime — variables die when their block ends

> **When a block finishes executing, every variable declared inside it is destroyed and removed from memory.** Accessing it afterwards isn't just disallowed — the variable literally no longer exists. To use a value beyond a block, declare the variable **outside** that block.

#### Global scope (class-level / static fields)

A variable declared **outside every function**, directly inside the class, is accessible from **anywhere in that class** — every function can see it.

```java
public class Demo {
    static String name = "Aditya";      // global scope — visible everywhere in this class

    public static void main(String[] args) {
        System.out.println(name);       // works
    }

    static void fun() {
        System.out.println(name);       // also works
    }
}
```

> **Note:** for now, such variables must be declared `static` for `main`/other `static` functions to access them — the reason behind this requirement (and why `static` keeps showing up) is deferred until OOP.

| Scope | Where declared | Visible where |
|---|---|---|
| **Local** | Inside a `{ }` block (function, `if`, loop) | Only within that block |
| **Global** (class-level) | Outside all functions, inside the class | Anywhere in the class |



### 11. Recursion

> **Recursion = a function that calls itself.**

Recall function chaining (`A → B → C`). Recursion is the special case where the callee is the **same function** as the caller.

#### Naive (broken) recursion — infinite

```java
static void a() {
    a();    // calls itself unconditionally, forever — never terminates
}
```

Left as-is, this recurses infinitely (in practice, eventually crashing with a `StackOverflowError`, since each call consumes stack memory that's never released).

#### Fixing it: the base case

> **Base case** = a condition under which the function stops calling itself and simply returns — the exit ramp that prevents infinite recursion.

Every correct recursive function has **two essential parts**:

```
1. RECURSIVE CASE — the function calls itself with a "smaller"/simpler input.
2. BASE CASE       — a condition where it stops recursing and returns directly.
```

#### Worked example: print 1 to n recursively

```java
static void printNum(int n) {
    if (n == 0) {
        return;                // BASE CASE — stop when n reaches 0
    }
    printNum(n - 1);           // RECURSIVE CALL — smaller input
    System.out.println(n);     // runs AFTER the recursive call returns
}
```

Call `printNum(5)`:

#### Phase 1 — going DOWN (recursive calls stack up)

```
printNum(5) → n≠0, calls printNum(4)
  printNum(4) → n≠0, calls printNum(3)
    printNum(3) → n≠0, calls printNum(2)
      printNum(2) → n≠0, calls printNum(1)
        printNum(1) → n≠0, calls printNum(0)
          printNum(0) → n==0 → BASE CASE → return
```

Nothing has printed yet — every call is still **paused**, waiting for the deeper call to finish, at the line right after `printNum(n-1)`.

#### Phase 2 — unwinding back UP (each paused call resumes and prints)

```
printNum(0) returns → printNum(1) resumes → prints 1 → returns
printNum(1) returns → printNum(2) resumes → prints 2 → returns
printNum(2) returns → printNum(3) resumes → prints 3 → returns
printNum(3) returns → printNum(4) resumes → prints 4 → returns
printNum(4) returns → printNum(5) resumes → prints 5 → returns
```

**Output:** `1 2 3 4 5`

> **Why it prints 1→5 (ascending) even though the calls counted DOWN from 5→0:** the print statement sits **after** the recursive call, so it only executes during the unwinding phase — in the reverse order the calls were made. Moving `System.out.println(n)` **before** the recursive call would print `5 4 3 2 1` instead (descending), since printing would happen on the way *down*.

```java
// Prints 5 4 3 2 1 (printing BEFORE the recursive call → happens on the way down)
static void printDesc(int n) {
    if (n == 0) return;
    System.out.println(n);
    printDesc(n - 1);
}
```

#### When to use recursion instead of a loop

For simple tasks like counting 1→n, a plain `for` loop is simpler and usually preferable. Recursion earns its keep for problems that are **naturally self-similar** — where the solution to a problem depends on solutions to smaller versions of the same problem — and where a loop-based solution would be significantly more awkward to write. This is very common in **Data Structures & Algorithms** problems (trees, divide-and-conquer, backtracking, etc.).



### 12. Recursion in Depth: The Fibonacci Example

**Fibonacci sequence:** each number is the sum of the previous two.

```
Index:  0  1  2  3  4  5  6 ...
Value:  1  1  2  3  5  8  13 ...
```

*(Note: the instructor's convention starts the sequence with 1, 1 — a common alternative starts with 0, 1. The base cases below match the 1, 1 convention used in the lecture.)*

#### Mathematical definition (which translates directly into code)

```
fib(n) = fib(n-1) + fib(n-2)         (recursive case)
fib(0) = 1, fib(1) = 1               (base cases)
```

#### Recursive code

```java
static int fib(int n) {
    if (n == 0 || n == 1) {
        return 1;               // BASE CASES
    }
    int x = fib(n - 1);         // solve the smaller problem #1
    int y = fib(n - 2);         // solve the smaller problem #2
    return x + y;               // combine the two results
}
```

#### The key mental model for writing recursion

> **Don't trace the entire call tree in your head while writing the code.** Instead: *assume* the recursive calls (`fib(n-1)`, `fib(n-2)`) will magically work correctly, and just write the logic that **combines** their results into the answer for `n`. Recursion handles the rest by applying the same reasoning at every smaller level, until it bottoms out at the base cases.

#### The call tree for `fib(5)`

Unlike `printNum` (a single straight chain), Fibonacci makes **two** recursive calls per invocation — so the calls branch into a **tree**:

```
                          fib(5)
                    /                \
              fib(4)                  fib(3)
             /      \                /      \
        fib(3)      fib(2)       fib(2)    fib(1)=1
        /   \       /   \        /   \
    fib(2) fib(1) fib(1) fib(0) fib(1) fib(0)
    /   \
fib(1) fib(0)
```

- The **leaves** of the tree are all base cases (`fib(1)` or `fib(0)`, each returning `1`).
- Results bubble **up** the tree: each parent node sums its two children's returned values.
- Execution order: depth-first, **left branch fully resolved before the right branch begins**.

#### Complexity note (a preview, not fully covered)

Notice that `fib(3)` and `fib(2)` appear **multiple times** in the tree above — the same sub-problem gets recomputed repeatedly. This redundant work is why naive recursive Fibonacci is inefficient (exponential time), and is the motivation for a later DSA technique called **memoization / dynamic programming**. Not covered here — flagging it because it's the classic follow-up interview question.



#### Quick Self-Check

> **Q1.** What's the difference between a parameter and an argument?

*Answer:* A parameter is the variable declared in the function's definition (e.g., `int a` in `static int sum(int a, int b)`), acting as a placeholder for input. An argument is the actual value supplied when calling the function (e.g., the `5` in `sum(5, 6)`).

> **Q2.** Why can't two functions differ only by return type?

*Answer:* Because a caller can call a function and simply ignore its return value, so the call `fun();` looks identical regardless of what `fun` returns. The compiler has no way to decide which of two same-named, same-parameter functions the programmer intended, so Java disallows it.

> **Q3.** Why does `printNum(5)` (with the print *after* the recursive call) print `1 2 3 4 5` rather than `5 4 3 2 1`?

*Answer:* The recursive call happens first, going all the way down to `n == 0` before anything prints. Each call then resumes in reverse order as the stack unwinds — `printNum(1)` finishes its print first, then `printNum(2)`, etc. — producing ascending output.

> **Q4.** Why is a base case mandatory in recursion?

*Answer:* Without a base case, the function calls itself indefinitely, consuming stack memory until the program crashes (`StackOverflowError`). The base case provides the terminating condition where the function returns directly instead of recursing further.



#### Golden Rules / Checklist

- [ ] A **function** is a named, reusable block of code — write logic once, call it many times.
- [ ] Function anatomy: `static returnType name(parameterList) { body; return value; }` (`static` is a black-box placeholder for now).
- [ ] **Parameters** = declared placeholders in the definition; **Arguments** = actual values passed at call time.
- [ ] Four function types by I/O: no-in/no-out, in/no-out, no-in/out, in/out.
- [ ] `void` = returns nothing; `return;` in a `void` function is optional.
- [ ] A caller may **ignore** a function's return value — this fact is exactly why return-type-only overloading is illegal.
- [ ] **`main` is itself a function** — the JVM's entry point; other functions only run if (transitively) called from `main`.
- [ ] **Overloading** = same name, different parameter list (differ by number, type, or order of parameters) — never by return type alone.
- [ ] In chained calls, execution goes deep first, then unwinds in **reverse order** — each caller resumes only after its callee finishes.
- [ ] **Scope:** a variable lives only inside the `{ }` block it's declared in (local scope); destroyed when that block ends. A variable declared at class level (outside all functions) has global scope within that class.
- [ ] **Recursion** = a function calling itself. Every correct recursive function needs (1) a **recursive case** with a smaller input and (2) a **base case** that stops the recursion.
- [ ] Recursive execution has two phases: **going down** (calls stack up) and **unwinding up** (paused calls resume in reverse) — where you place work relative to the recursive call determines the output order.
- [ ] When *writing* recursion, trust the recursive call to work correctly and just define how to combine its result; don't try to mentally trace the whole tree while writing.
- [ ] Recursive functions that call themselves **more than once** (like Fibonacci) produce a **call tree**, not a chain — and often repeat sub-problems (a motivation for memoization/DP, covered in DSA).

#### Practice Questions

**Basic**
1. Write a function `isEven(int n)` that returns a `boolean`, and show how to call it from `main`.
2. What's the difference between a function with return type `void` and one with return type `int`?    
3. Identify the parameters and arguments in: `static int add(int x, int y) {...}` called as `add(3, 4)`.     

**Intermediate**
4. Explain why the following pair of overloads is illegal, and fix it:
   ```java
   static int compute(int a) { return a * 2; }
   static double compute(int a) { return a * 2.5; }
   ```
5. Trace the output of this code and explain the order:
   ```java
   static void a() { b(); System.out.println("A"); }
   static void b() { c(); System.out.println("B"); }
   static void c() { System.out.println("C"); }
   // main calls a()
   ```
6. Why does this code fail to compile? Show two different fixes.
   ```java
   public static void main(String[] args) { int x = 5; helper(); }
   static void helper() { System.out.println(x); }
   ```
7. Write a recursive function `printDesc(int n)` that prints `n` down to `1`, then modify it to print `1` up to `n` — explain what single change accomplishes the reversal.

**Advanced / Interview-style**
8. Explain, in terms of the call stack, exactly what happens when a recursive function has no base case. What is the eventual runtime outcome?   
9. For the recursive `fib(n)` above, how many total calls are made to compute `fib(5)`? Why does the count grow so fast, and what technique would fix it?   
10. A candidate says "recursion and loops are interchangeable, so recursion is never necessary." Give one type of problem where recursion is significantly cleaner than a loop, and explain why (hint: self-similar structure).     
11. Explain why Java doesn't let you decide which of two overloaded methods to call based on the *type you intend to assign the result to* (e.g., `int x = fun();` vs. `String s = fun();`).    


## 12. Object-Oriented Programming (OOP) — Introduction

### 1. The Problem with Traditional Programming

Before OOP, when you needed to represent a **Student** entity, you'd create independent, scattered variables:

```java
String name = "Aditya";
int age = 28;
int rollNumber = 1001;
String college = "IIT Guwahati";
```

#### Problems with this approach

| # | Problem | Why it hurts |
|---|---------|--------------|
| 1 | **Too many independent variables** | No logical grouping. `name`, `age`, `rollNumber`, `college` are unrelated in the eyes of the compiler. |
| 2 | **Passing to functions is painful** | Every function needs all 4 variables passed separately. Miss one → broken. |
| 3 | **New student = new set of variables** | `name2`, `age2`, `rollNumber2`... naming becomes a mess. Doesn't scale. |
| 4 | **No authority / ownership over data** | Any code anywhere can modify these variables. No one "owns" the data. |

```java
// Traditional — ugly and error-prone
String name  = "Aditya";
int    age   = 28;
int    rollNumber = 1001;
String college    = "IIT Guwahati";

String name2  = "Rohit";
int    age2   = 28;
int    rollNumber2 = 102;
String college2    = "IIT Guwahati";

// To print a student you MUST pass all 4 individually
static void print(String name, int age, int rollNumber, String college) {
    System.out.println(name + " " + age + " " + rollNumber + " " + college);
}
```


### 2. What is OOP?

**Object-Oriented Programming (OOP)** is a **programming paradigm** — a *way of thinking and writing code*.

#### Core Philosophy
> "If you want to solve a **real-world problem**, first **mimic the real world** in your programming."

- In the real world, a **Student** is an *object* — one entity with multiple attributes.
- OOP says: represent it as **one object** in code too, not as scattered variables.

#### Key insight on "mimicking"
You do **NOT** need to copy the entire real world. A human plays many roles — student, son/daughter, employee. You only model the **characteristics relevant to your problem**.

```
Real World                     Programming World
─────────────────              ─────────────────────────────
Student (abstract idea)   →   class Student { ... }
Aditya (a real student)   →   Student s1 = new Student();
Rohit  (a real student)   →   Student s2 = new Student();
```

#### Other paradigms (for context)
- **Procedural** — code as a sequence of instructions (C)
- **Functional** — code as mathematical functions (Haskell, parts of Java 8+)
- **OOP** — code modelled around objects (Java, C++, Python)


### 3. Classes — The Blueprint

A **class** is a blueprint/template that describes *what an object looks like* — its structure and behaviour. **No memory is allocated** for a class alone.

```java
class Student {
    // Fields (Characteristics / State)
    String name;
    int    age;
    int    rollNumber;
    String college;
}
```

### Analogy
Think of a class like an **architectural blueprint** of a house. The blueprint itself is not a house — it just tells you how a house will be built. The actual house (object) is built later.


### 4. Objects — The Real Instance

An **object** is a concrete instance of a class. This is where actual memory gets allocated.

#### Creating an Object — Syntax

```java
// Declaration + Definition (two separate lines)
Student s1;               // Step 1: Declare reference variable
s1 = new Student();       // Step 2: Allocate memory and assign

// Combined (preferred shorthand)
Student s1 = new Student();
```

#### Setting field values using the dot (`.`) operator

```java
s1.name       = "Aditya";
s1.age        = 28;
s1.rollNumber = 1001;
s1.college    = "IIT Guwahati";

Student s2 = new Student();
s2.name       = "Rohit";
s2.age        = 28;
s2.rollNumber = 102;
s2.college    = "IIT Guwahati";
```

#### Accessing fields

```java
System.out.println(s1.name);       // Aditya
System.out.println(s2.rollNumber); // 102
```

#### The `new` keyword
- `new` tells the JVM: **"Allocate a chunk of memory in the Heap for this object."**
- It returns the **starting memory address** of that allocated block.
- This is **Dynamic Memory Allocation** (happens at Run Time).



### 5. Memory Model: Stack vs Heap

```
┌─────────────────────────────────────────────┐
│                  HEAP MEMORY                │
│                                             │
│   ┌──────────────────┐ ┌─────────────────┐  │
│   │  Object (unnamed)│ │ Object (unnamed)│  │
│   │  name = "Aditya" │ │  name = "Rohit" │  │
│   │  age  = 28       │ │  age  = 28      │  │
│   │  rollNo = 1001   │ │  rollNo = 102   │  │
│   │  college = "IIT" │ │  college = "IIT"│  │
│   │  [addr: 1001]    │ │  [addr: 2001]   │  │
│   └──────────────────┘ └─────────────────┘  │
└─────────────────────────────────────────────┘

┌─────────────────────────┐
│      STACK MEMORY       │
│  s1 → 1001 (address)    │
│  s2 → 2001 (address)    │
└─────────────────────────┘
```

| Memory Region | Stores | Managed by |
|---|---|---|
| **Heap** | Actual objects (anonymous, unnamed) | Garbage Collector (JVM) |
| **Stack** | Reference variables (named), primitive locals | Automatically (method scope) |

> **Tip:** Every time you call `new`, a new block in the Heap is created. The Stack only holds the *reference* (address) pointing to that block.



### 6. Reference Variables

```java
Student s1 = new Student();
```

- `s1` is **NOT** the object. It is a **reference variable**.
- It lives in the **Stack** and stores the **memory address** of the actual object in the **Heap**.
- The actual object in the Heap is **unnamed** — it has no name of its own in Java.

```
s1  ──────────────────►  [ Student object @ Heap address 1001 ]
                          { name, age, rollNumber, college }
```

#### The dot operator (`.`)
The `.` operator **dereferences** the reference variable — it follows the stored address to reach the actual object in Heap, then accesses the requested field or method.

```java
s1.name              // Follow s1's address → reach object → get 'name' field
s1.markAttendance()  // Follow s1's address → reach object → call method
```

> **Interview question:** *"Is `s1` an object?"*  
> **Answer:** No. `s1` is a **reference variable** that *points to* an object. The actual object is in Heap and is anonymous.



### 7. Compile Time vs Run Time / Static vs Dynamic Memory

| Term | When it happens | Example |
|---|---|---|
| **Compile Time** | During code compilation (before running) | Syntax errors, type checking |
| **Run Time** | While the program is actually executing | Object creation, logic execution |
| **Static Memory Allocation** | Compile Time | `int x = 4;` → stack frame allocated when method compiles |
| **Dynamic Memory Allocation** | Run Time (via `new`) | `new Student()` → heap block allocated while program runs |

```java
int x = 4;                      // Static allocation — compile time, goes to Stack
Student s1 = new Student();     // Dynamic allocation — run time, object goes to Heap
```

> **Note:** The `new` keyword is the trigger for **dynamic (heap) memory allocation** in Java. This is why object memory is flexible and not fixed at compile time.



### 8. Primitive vs Non-Primitive (User-Defined) Data Types

```
Data Types in Java
├── Primitive (built-in)
│   ├── Integer types  → byte, short, int, long
│   ├── Floating point → float, double
│   ├── Boolean        → boolean
│   └── Character      → char
│
└── Non-Primitive (Reference Types)
    ├── Built-in       → String, Arrays
    └── User-Defined   → Your own classes (e.g., Student, BankAccount)
```

When you write `class Student { ... }` and then `Student s1 = new Student();`:
- You have **created your own data type** called `Student`.
- This is called a **User-Defined Data Type**.
- Just like `int x` declares an integer, `Student s1` declares a Student-type reference.



### 9. Characteristics (Fields) vs Behaviours (Methods)

A real-world object has two aspects — and OOP models **both** inside the class:

| Aspect | Real World | In Java |
|---|---|---|
| **Characteristics** (State/Data) | Student's name, age, roll no. | Fields / Instance variables |
| **Behaviours** (Actions) | Student marks attendance, prints details | Methods (functions inside a class) |

```java
class Student {

    // ── FIELDS (Characteristics / State) ──────────────────────
    String name;
    int    age;
    int    rollNumber;
    String college;

    // ── METHODS (Behaviours) ───────────────────────────────────

    // Behaviour 1: Mark attendance
    void markAttendance() {
        // 'name' here refers to THIS object's own name field
        // No parameter needed — the method already has access to it!
        System.out.println("Attendance marked by " + name);
    }

    // Behaviour 2: Print all details
    void print() {
        // Again, no parameters needed — fields are directly accessible
        System.out.println(name + ", " + age + ", " + rollNumber + ", " + college);
    }
}
```

### Why put `print()` inside the class?

- **Before OOP:** `print(String name, int age, int rollNo, String college)` — you had to pass all 4 parameters manually.
- **With OOP:** `s1.print()` — the method *already has access* to all fields of its own object. **Zero parameters needed.**

> **Tip:** Methods inside a class have **implicit access** to all fields of that object — no need to pass them as arguments. This is the beginning of encapsulation.



### 10. Naming Conventions

These are **conventions (good practices)**, not compiler-enforced rules. Breaking them won't cause errors, but it's unprofessional.

| Element | Convention | Example |
|---|---|---|
| **Variables** (primitive & reference) | `camelCase` — first word lowercase, subsequent words capitalized | `firstName`, `rollNumber`, `s1` |
| **Classes** | `PascalCase` — every word starts with capital | `Student`, `BankAccount`, `MyClass` |
| **Methods / Functions** | `camelCase` — same as variables | `markAttendance()`, `printDetails()` |
| **Constants** | `UPPER_SNAKE_CASE` | `MAX_SIZE`, `PI` |

```java
// ✅ Correct
class BankAccount {
    int accountNumber;
    void depositMoney() { }
}

// ❌ Avoid (works but bad practice)
class bank_account {
    int AccountNumber;
    void DepositMoney() { }
}
```



### 11. Physical vs Non-Physical Objects

OOP isn't limited to mimicking physical things. **Any complex concept** with multiple attributes and behaviours can be a class.

| Object | Physical? | Example fields |
|---|---|---|
| `Student` | ✅ Yes | name, age, rollNumber |
| `Animal` | ✅ Yes | type, numberOfLegs, isWild |
| `BankAccount` | ❌ No | accountNumber, ifscCode, branch, balance |
| `Location` | ❌ No | latitude, longitude, altitude |
| `Order` | ❌ No | orderId, items, totalPrice, status |

> **Note:** The rule of thumb: if something is **too complex for a single primitive variable** and has multiple related attributes — model it as a class.


### 12. Java is (Almost) Purely OOP

In Java, **everything lives inside a class**. You cannot write even a single line of executable code without a class.

```java
// Minimum valid Java program
public class Demo {                            // ← Must have a class
    public static void main(String[] args) {   // ← Must have main method
        // Your code goes here
    }
}
```

- `Demo` is a class.
- `main` is a method (behaviour) of `Demo`.
- You declare and use other objects (like `Student`) inside `main`.

**Why "almost" purely OOP?**
- Java has **primitive types** (`int`, `char`, `boolean`, etc.) which are NOT objects.
- A truly pure OOP language (like Smalltalk) has no primitives — everything is an object.
- Java keeps primitives for performance. (Note: wrapper classes `Integer`, `Character`, etc. give them object-like capabilities.)



### 13. Full Working Code Example

```java
// ─── Student class (can be in same file or separate Student.java) ───

class Student {

    // Fields — Characteristics
    String name;
    int    age;
    int    rollNumber;
    String college;

    // Method — Behaviour 1
    void markAttendance() {
        System.out.println("Attendance marked by " + name);
    }

    // Method — Behaviour 2
    void print() {
        System.out.println("Name: "      + name
                         + ", Age: "     + age
                         + ", Roll No: " + rollNumber
                         + ", College: " + college);
    }
}

// ─── Main class — Entry point ───

public class Demo {
    public static void main(String[] args) {

        // Create object s1 (Aditya)
        Student s1 = new Student();   // 'new' → allocates heap memory
        s1.name       = "Aditya";
        s1.age        = 28;
        s1.rollNumber = 1001;
        s1.college    = "IIT Guwahati";

        // Create object s2 (Rohit)
        Student s2 = new Student();   // separate heap block
        s2.name       = "Rohit";
        s2.age        = 28;
        s2.rollNumber = 102;
        s2.college    = "IIT Guwahati";

        // Call behaviours
        s1.markAttendance();  // Attendance marked by Aditya
        s2.markAttendance();  // Attendance marked by Rohit

        s1.print();  // Name: Aditya, Age: 28, Roll No: 1001, College: IIT Guwahati
        s2.print();  // Name: Rohit,  Age: 28, Roll No: 102,  College: IIT Guwahati
    }
}
```

**Output:**
```
Attendance marked by Aditya
Attendance marked by Rohit
Name: Aditya, Age: 28, Roll No: 1001, College: IIT Guwahati
Name: Rohit, Age: 28, Roll No: 102, College: IIT Guwahati
```



### 14. Key Interview Points

| Question | Answer |
|---|---|
| Is `s1` an object? | ❌ No. `s1` is a **reference variable** stored in Stack, pointing to an unnamed object in Heap. |
| Where are objects stored? | **Heap memory** |
| Where are reference variables stored? | **Stack memory** |
| What does `new` do? | Allocates a block in Heap at **runtime** (dynamic memory allocation) |
| What is a class? | A **blueprint/template** for an object — no memory allocated for the class itself |
| What is an object? | A **runtime instance** of a class — actual memory allocated in Heap |
| What is a User-Defined Data Type? | A class you create yourself (e.g., `Student`) as opposed to built-in types |
| Why are objects called non-primitive types? | Because they can't be represented by a single value; they're containers holding multiple values |
| What is the dot (`.`) operator? | It **dereferences** a reference variable — follows the Heap address to access fields/methods |
| Why does Java have primitives if it's OOP? | Performance. Pure OOP with no primitives is slower. Wrapper classes (`Integer`, etc.) bridge the gap. |
| What is dynamic memory allocation? | Memory allocated at **runtime** using `new`, as opposed to static allocation at compile time |



### 15. Quick Summary / Mental Model

```
REAL WORLD                    JAVA (Programming World)
──────────────────────────    ──────────────────────────────────────
Concept/Blueprint of          class Student { }
a "Student"                   ↑ Just a blueprint, NO memory allocated

A specific student            Student s1 = new Student();
"Aditya"                      ↑ Object created in Heap,
                                s1 (reference var) in Stack

Student's attributes          Fields: name, age, rollNumber, college
(characteristics/state)

Student's actions             Methods: markAttendance(), print()
(behaviours)

Access attributes/methods     Dot operator:  s1.name  /  s1.print()
```

> **Coming in future videos — The 4 Pillars of OOP:**
> 1. **Encapsulation** — bundling data + methods together, controlling access
> 2. **Inheritance** — a class can inherit from another class
> 3. **Polymorphism** — one interface, many implementations
> 4. **Abstraction** — hiding implementation details, showing only what's necessary


## 13. Constructors, `this` Keyword & Constructor Chaining

> Source: Coder Army Core Java series, Lecture 13 (continuation of OOP)
> Depth level: 2–3 YOE — covers default values, the "when does Java auto-generate a constructor" rule, `this(...)` chaining rules, and the classic interview traps


### 1. Real-World Hook

A Swiggy `Order` object is created the moment you tap "Place Order". If the object comes into existence with `orderId = 0`, `restaurant = null`, `status = null`, any downstream code (payment, delivery assignment) can crash or, worse, silently process garbage. You want the object to be **valid from the instant it is created**. That is the problem constructors solve.


### 2. Default Values of Instance Variables

```java
class Student {
    String name;
    int age;
    int rollNumber;
    String college;
}

Student s1 = new Student();
System.out.println(s1.name + " " + s1.age + " " + s1.rollNumber + " " + s1.college);
// null 0 0 null
```

No compile error — instance variables get **default values** automatically.

| Data type | Default value |
|---|---|
| Integer types (`byte`, `short`, `int`, `long`) | `0` |
| Floating point (`float`, `double`) | `0.0` |
| `boolean` | `false` |
| `char` | `'\u0000'` (null character) — not covered in the lecture, but the same rule |
| Any non-primitive / reference (`String`, arrays, objects) | `null` |

> **`null` = "this reference points to nothing."** Reference variables (like `String college`) hold an address; `null` means no address yet. (`NullPointerException` and why `null` is dangerous come later.)


### 3. Instance Variables vs. Local Variables

| | Instance variable | Local variable |
|---|---|---|
| Declared | Inside a class, outside methods | Inside a method / block |
| Lives in | **Heap** (as part of the object) | **Stack** (for primitives) |
| Lifetime | As long as the object lives | Until its block/method ends |
| Default value | **Yes** (0 / 0.0 / false / null) | **No** — must be initialized before use |

```java
public static void main(String[] args) {
    int x;
    System.out.println(x);   // WRONG: compile error — "variable x might not have been initialized"
}
```

**Why the difference (the instructor's reasoning):** objects on the heap tend to live long and be reused, so Java guarantees a safe starting state. Local variables have very limited scope, so Java skips auto-initialization (an optimization) and instead **forces you to assign a value** before reading — this also catches a whole class of bugs at compile time.

- Class-body variables → called **instance variables** (they represent an object's *data/characteristics*).
- Class-body methods → called **instance methods** (they represent *behaviours*).

### 4. The Problem Constructors Solve

```java
Student s1 = new Student();
s1.name = "Aditya";
s1.age = 28;
s1.rollNumber = 1001;
s1.college = "IIT Guwahati";
```

Problems:
1. **Tedious** — one line per field, per object.
2. **Easy to forget** a field (e.g., someone later adds `grades` and you never set it) → silently gets the default value.
3. Nothing **forces** you to provide valid data at creation time.

We want to supply values **at the moment the object is constructed**.



### 5. What Is a Constructor

> A **constructor** is a special method that runs when an object is created and is used to **initialize** that object's state.

```java
class Student {
    String name; int age; int rollNumber; String college;

    Student() {                    // constructor
        name = "Aditya";
        age = 28;
        rollNumber = 1001;
        college = "IIT Guwahati";
    }
}

Student s1 = new Student();        // all four fields are set automatically
```

#### Rules of a constructor

1. **Name must be exactly the class name.**
2. **No return type — not even `void`.**
3. It is invoked as part of object creation (`new ClassName(...)`).
4. Its purpose is to **initialize** the new object.
5. It can be **overloaded**, like any method.

```java
// WRONG: adding a return type turns it into an ordinary method that merely happens to share the class name
void Student() { ... }

// RIGHT: no return type at all
Student() { ... }
```

#### What `new Student()` actually does

```
Student s1 = new Student();
   │            │      └── () = the CONSTRUCTOR CALL
   │            └── 'new' allocates the object in heap memory
   └── s1 (a reference variable in stack) stores the object's address
```

> **Correction to a common belief:** the constructor isn't "automatically called by magic." The `Student()` part of `new Student()` **is** the call — it's just built into the syntax, so we don't notice it as a method call.

```
STACK                    HEAP
┌────────┐               ┌───────────────────────────────┐
│ s1→addr│──────────────→│ name="Aditya", age=28,        │
└────────┘               │ rollNumber=1001, college=...  │
                         └───────────────────────────────┘
```


### 6. Default Constructor

> **Java requires every class to have a constructor.** If you don't write any, the compiler silently inserts an empty **default constructor**.

```java
class Student {
    String name; int age;
    // you wrote no constructor →  Java adds:  Student() { }
}
```

- The compiler-generated one takes no parameters and has an empty body, so fields keep their **default values** (null/0/false).
- This is why `new Student()` always worked in earlier lectures even though we never wrote a constructor.

#### The critical rule

> **The default constructor is generated ONLY if you define NO constructor of your own.** The moment you write *any* constructor (default-style or parameterized), Java stops generating one.

```java
class Student {
    Student(String n, int a) { ... }     // you defined one constructor
}

Student s2 = new Student();              // WRONG: compile error — "constructor Student() is undefined"
```

Fix: add your own no-arg constructor explicitly (see overloading below).



### 7. Parameterized Constructor

Hard-coding "Aditya/28/1001" inside the constructor is useless for real objects. Instead, accept values as parameters:

```java
class Student {
    String name; int age; int rollNumber; String college;

    Student(String n, int a, int rn, String c) {
        name = n;
        age = a;
        rollNumber = rn;
        college = c;
    }
}

Student s1 = new Student("Aditya", 28, 1001, "IIT Guwahati");
```

Benefits: values are supplied **at creation**, and the compiler **forces** you to supply all of them (you can't forget one).



### 8. Constructor Overloading

Same idea as method overloading — multiple constructors, different parameter lists:

```java
Student() { }                                            // no-arg
Student(String n, int a, int rn, String c) { ... }       // full

Student s1 = new Student();                              // valid — no-arg exists
Student s2 = new Student("Rohit", 28, 102, "IIT Guwahati");
```

Overloading legality is identical to methods (differ by **number**, **type**, or **order** of parameters; never by return type — constructors have none).


### 9. The `this` Keyword

> **`this` = a reference to the current object** (the object on which the constructor or method is currently executing).

#### Use 1 — resolve name clashes between parameters and fields

```java
// WRONG: parameter shadows the field — assigns the parameter to itself, field never set
Student(String name, int age) {
    name = name;        // compiler warning: "assignment to variable name has no effect"
    age = age;
}

// RIGHT: 'this.field' = the object's field; bare name = the parameter
Student(String name, int age) {
    this.name = name;
    this.age = age;
}
```

- `this.name` → the **instance variable** of the object being built.
- `name` (bare) → the **parameter** (local scope wins when both share a name).
- Convention: use the **same names** for parameters and fields plus `this.` — it's the most readable form. (If names differ, `this.` is optional, since there's no ambiguity.)

> Think of `this` as the same kind of thing as `s1` in `s1.name`, except `s1` is the *outside* name of the object and `this` is the object's way of referring to *itself* from the inside.

#### Use 2 — call another constructor of the same class: `this(...)`

Covered in the next section.



### 10. Constructor Chaining with `this(...)`

Suppose you want to allow creating a `Student` with 0, 1, 2, 3 or 4 pieces of information. Naively:

```java
Student() { }
Student(String name) { this.name = name; }
Student(String name, int age) { this.name = name; this.age = age; }
Student(String name, int age, int rollNumber) { this.name = name; this.age = age; this.rollNumber = rollNumber; }
Student(String name, int age, int rollNumber, String college) {
    this.name = name; this.age = age; this.rollNumber = rollNumber; this.college = college;
}
```

Assignment logic is **duplicated** across constructors. Instead, make every constructor delegate to the **most complete one**.

#### Style A — every constructor calls the full constructor

```java
Student() {
    this("Unknown", 0, 0, "Unknown");
}
Student(String name) {
    this(name, 0, 0, "Unknown");
}
Student(String name, int age) {
    this(name, age, 0, "Unknown");
}
Student(String name, int age, int rollNumber) {
    this(name, age, rollNumber, "Unknown");
}
Student(String name, int age, int rollNumber, String college) {    // the ONLY one doing real assignment
    this.name = name;
    this.age = age;
    this.rollNumber = rollNumber;
    this.college = college;
}
```

#### Style B — each constructor calls the next-bigger one (a chain)

```java
Student()                      { this("Unknown"); }
Student(String name)           { this(name, 0); }
Student(String name, int age)  { this(name, age, 0); }
Student(String name, int age, int rollNumber) { this(name, age, rollNumber, "Unknown"); }
Student(String name, int age, int rollNumber, String college) { /* real assignments */ }
```

Also using `"Unknown"` instead of `null` for defaults is deliberate — avoiding `null` where you can prevents `NullPointerException`s later.

#### Execution order — trace `new Student()` in Style B (with a print in each constructor)

```
new Student()                → enters constructor #1 → its FIRST line is this("Unknown")
  → enters constructor #2   → first line is this(name, 0)
    → enters constructor #3 → first line is this(name, age, 0)
      → enters constructor #4 → first line is this(name, age, rollNumber, "Unknown")
        → enters constructor #5 → assigns fields → prints "I'm in fifth constructor"
      ← back in #4 → prints "I'm in fourth constructor"
    ← back in #3 → prints "I'm in third constructor"
  ← back in #2 → prints "I'm in second constructor"
← back in #1 → prints "I'm in first constructor"
```

**Output order: fifth, fourth, third, second, first** — same "go deep, then unwind" behaviour as function chaining/recursion.

#### Rule: `this(...)` must be the FIRST statement

```java
// WRONG: compile error — "call to this must be first statement in constructor"
Student(String name) {
    System.out.println("hi");
    this(name, 0);
}

// RIGHT
Student(String name) {
    this(name, 0);
    System.out.println("hi");
}
```

> Why: the object must be fully initialized by the delegated constructor **before** any of this constructor's own code runs. (Java also forbids a constructor cycle — `A()` → `B()` → `A()` — as a compile error.)



### 11. Interview Traps

#### Can you call a constructor manually, like a normal method?

> **No.** A constructor can be invoked in exactly **two** ways:
> 1. With `new ClassName(...)` (object creation), or
> 2. From another constructor of the same class via `this(...)` (or a parent's via `super(...)` — next lecture).

```java
Student s = new Student("A", 1, 2, "X");
s.Student("B", 3, 4, "Y");     // WRONG: not allowed — constructors aren't ordinary methods
```

#### What if `new` can't get memory?

`new` allocates on the **heap at runtime**. If the heap is full and cannot satisfy the request, the JVM throws an error at runtime.

> **Precision note:** the instructor calls this a "runtime exception"; strictly, it is `java.lang.OutOfMemoryError` — a subclass of **`Error`**, not `Exception`. Good interview answer: *"`new` can fail with `OutOfMemoryError` if the heap has insufficient space; it's an unchecked `Error`, typically not something you catch."*



#### Quick Self-Check

> **Q1.** Why does `new Student()` compile even if the class has no constructor, but fail after you add only a parameterized constructor?

*Answer:* If a class declares no constructor, the compiler inserts an empty default one. Once you declare any constructor, the compiler no longer generates the default — so `new Student()` has no matching constructor unless you write a no-arg one yourself.

> **Q2.** Why don't local variables get default values while instance variables do?

*Answer:* Instance variables live in heap objects that persist and are shared, so Java guarantees a safe starting state. Local variables have tiny scope, so Java skips default initialization (optimization) and instead forces you to assign before use, turning "forgot to initialize" into a compile error.

> **Q3.** In `Student(String name) { name = name; }`, what happens?

*Answer:* The parameter shadows the field, so the parameter is assigned to itself and the field is never set (compiler warns "assignment has no effect"). Fix with `this.name = name;`.

> **Q4.** What is the output order for a 5-level `this(...)` constructor chain that prints in each constructor?

*Answer:* The deepest constructor's print comes first; prints appear in the reverse order of the calls (fifth, fourth, third, second, first), because each constructor finishes only after the one it delegated to returns.

#### Golden Rules / Checklist

- [ ] Instance variables get defaults: numeric `0`, floating `0.0`, `boolean false`, references `null`. **Local variables get none** — must be initialized before use.
- [ ] Instance variables/methods = data/behaviour of an object; instance variables live in the **heap** inside the object.
- [ ] Constructor rules: **same name as class**, **no return type (not even `void`)**, used to initialize the object, can be overloaded.
- [ ] `new ClassName(args)` = allocate object in heap **and** call the matching constructor; the reference is stored in a stack variable.
- [ ] **Default constructor is auto-generated only when you write no constructor.** Write any constructor → it's gone.
- [ ] Parameterized constructors force callers to supply required data at creation time.
- [ ] `this` = reference to the current object; use `this.field = param;` when names clash.
- [ ] `this(...)` calls another constructor of the same class; it **must be the first statement** in the constructor.
- [ ] Constructor chaining removes duplicated assignment logic — funnel everything into one "master" constructor.
- [ ] Prefer safe defaults like `"Unknown"` over `null` where reasonable.
- [ ] A constructor **cannot** be called manually like a method — only via `new` or `this(...)`/`super(...)`.
- [ ] `new` can fail with `OutOfMemoryError` when heap space is insufficient.



#### Practice Questions

**Basic**   
1. What are the five rules for writing a constructor?    
2. What default value does each of `int`, `double`, `boolean`, `String` instance variables get?    
3. Why does the following not compile? `int count; System.out.println(count);` (inside `main`)    

**Intermediate**    
4. A class `Payment` has only `Payment(int amount)`. Why does `new Payment()` fail, and give two ways to fix it.   
5. Rewrite this class so constructor logic is not duplicated, using `this(...)`:
   ```java
   Order() { id = 0; status = "NEW"; }
   Order(int id) { this.id = id; status = "NEW"; }
   Order(int id, String status) { this.id = id; this.status = status; }
   ```
6. Why does `this(...)` have to be the first line of a constructor?
  
**Advanced / Interview-style**   
7. Trace the output and explain the order: a 3-constructor chain where each prints "In constructor N" *after* its `this(...)` call.    
8. Explain why `void Student() { }` inside class `Student` is not a constructor, and what would happen when you call `new Student()`.    
9. Why can't you call a constructor manually on an existing object? What two mechanisms *can* invoke a constructor?    
10. Distinguish `OutOfMemoryError` from `Exception` — which one can `new` throw, and why is it typically not handled?   


## 14. Object Memory Layout, Call by Value & Shallow vs Deep Copy


### 1. How Big Is an Object?

Primitives have fixed sizes. Objects need a formula.

| Type | Size |
|---|---|
| `byte` / `boolean` | 1 byte |
| `short` / `char` | 2 bytes |
| `int` / `float` | 4 bytes |
| `long` / `double` | 8 bytes |
| **Reference** (any object variable) | **4 bytes** (compressed oops, default) or **8 bytes** (large heap / compression off) |

```java
class Student {
    String name;      // reference -> 4 bytes
    int    age;       // 4
    int    rollNumber;// 4
    String college;   // reference -> 4
}
```

Naive sum = 4 + 4 + 4 + 4 = **16 bytes**. That is **not** the object's size. The object also stores a **header** and may need **padding**.

> **Important:** a reference field counts only **4 (or 8) bytes**: the address. The `String` object it points to is a *separate* heap object and is **not** included. This is called the **shallow size**.

> **⚠ Correction (where references live):** the video says reference variables live on the stack. That is only true for **locals/parameters**. A reference that is a **field** (like `name` above) lives **inside the object on the heap**.

> **Note:** "objects always live on the heap" is the programming model. The JIT can use **escape analysis** to scalar-replace or eliminate allocations that never escape a method, so no heap object may exist at runtime.



### 2. Anatomy of an Object

```
┌────────────────────────────────────────────────────────────┐
│ OBJECT HEADER                                              │
│   ┌──────────────────────────────┬────────────────────┐    │
│   │ Mark word           (8 bytes)│ Class pointer      │    │
│   │ hash, GC age, lock state     │ (4 bytes, klass)   │    │
│   └──────────────────────────────┴────────────────────┘    │
│   (arrays add a 4-byte LENGTH field -> 16-byte header)     │
├────────────────────────────────────────────────────────────┤
│ INSTANCE DATA (fields; JVM may reorder them)               │
├────────────────────────────────────────────────────────────┤
│ PADDING (so total size is a multiple of 8)                 │
└────────────────────────────────────────────────────────────┘
```

| Part | Size | What it holds |
|---|---|---|
| **Mark word** | 8 B | Identity hash code (lazily computed), GC age/mark bits, lock state (thin lock / monitor pointer). Used by `synchronized`, `hashCode()`, GC |
| **Class pointer (klass word)** | 4 B (8 B if compressed class pointers off) | Pointer to the **class metadata in Metaspace** (field layout, vtable, name). Used by `getClass()`, virtual dispatch, `instanceof` |
| **Instance data** | sum of fields | Your fields (own + inherited) |
| **Padding** | 0-7 B | Round total up to a multiple of 8 |

> **⚠ Correction:** the video says the class pointer is "like the `this` reference stored in the object". It is not. It does **not** point to the object itself; it points to the **class's metadata (Klass) in Metaspace**, shared by all instances of that class.

> **Newer JDKs:** *compact object headers* shrink the header to **8 bytes** (JEP 450 experimental in JDK 24, JEP 519 product feature in JDK 25, opt-in via `-XX:+UseCompactObjectHeaders`). Verify on your JDK; the default header is still 12 bytes.

```
Memory picture of one Student (offsets illustrative, JVM may reorder fields)

offset  size  content
  0      8    mark word
  8      4    class pointer
 12      4    name       (reference)
 16      4    age
 20      4    rollNumber
 24      4    college    (reference)
 28      4    padding
────────────
 32 bytes total
```



### 3. Worked Calculation

```
size = align8( header (12) + fields (with alignment gaps) )
```

**Student:** 12 + 16 = 28 -> next multiple of 8 is 32 -> **4 bytes padding** -> **32 bytes**.

```java
class Person { byte age; }
```
12 (header) + 1 (byte) = 13 -> next multiple of 8 is **16** -> 3 bytes padding -> **16 bytes**.

> **⚠ Correction (arithmetic):** the video ends this example with **18 bytes**. That is wrong: 12 + 1 + 3 = **16**. An object's size is always a multiple of 8, so 18 is impossible.

| Class | Header | Fields | Sum | Padded size |
|---|---|---|---|---|
| `class Empty {}` | 12 | 0 | 12 | **16** |
| `class Person { byte age; }` | 12 | 1 | 13 | **16** |
| `class Student { String; int; int; String }` | 12 | 16 | 28 | **32** |
| `class L { long v; }` | 12 | 8 (needs 8-byte alignment) | 12 + 4 gap + 8 | **24** |
| `class M { int a; long b; }` | 12 | int fills the 4-byte gap | 12 + 4 + 8 | **24** |
| `Integer` (wrapper) | 12 | 4 | 16 | **16** |
| `int[10]` | 16 (12 + 4 length) | 40 | 56 | **56** |

Two things to notice in the table:
- **Internal padding:** a `long`/`double` must sit at an offset divisible by 8, so a gap can appear *inside* the object (`class L`).
- **Field reordering:** the JVM does not lay fields out in source order. It packs them to fill gaps (`class M` costs no more than `class L`).

> **Why `Integer` (16 B) + reference (4 B) = 20 B vs `int` = 4 B** is the reason boxed collections (`List<Integer>`) use several times the memory of `int[]`.



### 4. Why Padding? (Alignment)

The JVM aligns every object to **8 bytes** (`-XX:ObjectAlignmentInBytes=8`, default).

| Reason | Detail |
|---|---|
| **Hardware-friendly access** | Aligned words/fields can be read in one operation, and objects don't straddle cache-line boundaries as often |
| **Compressed oops** | Because every object address is a multiple of 8, the low 3 bits are always 0, so a **32-bit reference can be shifted left by 3** to address up to **32 GB**. This is why heaps just under ~32 GB keep 4-byte references and heaps above it jump to 8-byte references |

> **⚠ Refinement:** the video explains padding as "the CPU fetches 8-byte chunks". Directionally right, but the sharper interview answer is **alignment + compressed oops**. Also, padding isn't "optional": if the sum is already a multiple of 8, padding is simply 0.

> **Gotcha:** setting `-Xmx` above ~32 GB can make your app use **more memory than a 30 GB heap**, because every reference doubles from 4 to 8 bytes.



### 5. Verifying Sizes

```java
// Add dependency: org.openjdk.jol:jol-core
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

System.out.println(VM.current().details());                    // shows reference size, alignment
System.out.println(ClassLayout.parseClass(Student.class).toPrintable());  // offsets, gaps, total
```

| Tool | Use |
|---|---|
| **JOL (Java Object Layout)** | Exact layout of a class/instance (offsets, padding) |
| `Instrumentation.getObjectSize(obj)` | Shallow size from a java agent |
| `-XX:+PrintFlagsFinal` | See `UseCompressedOops`, `UseCompressedClassPointers`, `ObjectAlignmentInBytes` |
| Heap dump + Eclipse MAT / VisualVM | **Retained size**: the object plus everything only it keeps alive |

| Term | Meaning |
|---|---|
| **Shallow size** | The object itself (header + fields + padding). References count as 4/8 bytes only |
| **Retained size** | Memory freed if this object were collected (object + exclusively-owned referents) |

This is what the video computed: **shallow size**.



### 6. Call by Value: Primitives

```java
public class Demo {
    static void addTen(int x, int y) { x = x + 10; y = y + 10; }

    public static void main(String[] args) {
        int x = 4, y = 5;
        System.out.println(x + " " + y);   // 4 5
        addTen(x, y);
        System.out.println(x + " " + y);   // 4 5   <- NOT 14 15
    }
}
```

The `x` and `y` inside `addTen` are **new variables** in a new stack frame; the argument **values are copied** into them.

```
main frame                     addTen frame (new)
┌─────────────┐                ┌─────────────┐
│ x = 4       │ ── copy ────►  │ x = 4 -> 14 │
│ y = 5       │ ── copy ────►  │ y = 5 -> 15 │
└─────────────┘                └─────────────┘
 unchanged                      frame popped on return; copies vanish
```

> **Note:** `addTen` is `static` because `main` is static and can call only static methods directly. Instance methods need an object (`obj.method()`).



### 7. Call by Value: Objects (the part that confuses everyone)

```java
class Point { int x, y; Point(int x, int y) { this.x = x; this.y = y; } }

static void addTen(Point p) { p.x = p.x + 10; p.y = p.y + 10; }

Point r1 = new Point(4, 5);
addTen(r1);
System.out.println(r1.x + " " + r1.y);   // 14 15   <- changed!
```

Still call by value. **What is copied is the reference (the address), not the object.**

```
STACK                                          HEAP
main frame                                     ┌───────────────┐
┌──────────────┐                               │ Point         │
│ r1 = @1001 ──┼──────────────────────────────►│ x = 4 -> 14   │
└──────────────┘                               │ y = 5 -> 15   │
addTen frame                                   └───────────────┘
┌──────────────┐                                       ▲
│ p  = @1001 ──┼───────────────────────────────────────┘
└──────────────┘   (copy of the ADDRESS, same object)

addTen returns -> frame popped -> p disappears.
The Point object stays alive (r1 still reaches it) and keeps the changes.
```

> **⚠ Correction:** the video says that when `p` disappears "the garbage collector removes the pointer". No. `p` is a **local in the popped stack frame**; it goes away with the frame. The **GC** reclaims *objects* that are unreachable, and this object is still reachable through `r1`.

> **⚠ Correction:** the video presents its second example as "call by reference". The *effect* looks like it, but the mechanism is **always** call by value.



### 8. Java Is Strictly Pass-by-Value (Proof)

If Java were pass-by-reference, a `swap` would work. It doesn't.

```java
// ❌ WRONG expectation: "objects are passed by reference, so this swaps"
static void swap(Point a, Point b) { Point t = a; a = b; b = t; }

Point p2 = new Point(1, 2), p3 = new Point(3, 4);
swap(p2, p3);
System.out.println(p2.x + " " + p3.x);   // 1 3   <- NOT swapped
```

```java
// Reassigning the parameter only changes the LOCAL COPY of the address
static void reassign(Point p) {
    p = new Point(99, 99);   // p now points to a new object; caller's variable untouched
    p.x += 1;
}
```

```
Before reassign:   r1 ─► @1001 ─► Point(14,15)          p ─► @1001
After  p = new..:  r1 ─► @1001 ─► Point(14,15)          p ─► @2002 ─► Point(99,99)
```

| What you do to the parameter | Visible to caller? | Why |
|---|---|---|
| `x = x + 10` (primitive) | ❌ No | Copy of the value |
| `p.x += 10` (**mutate** the object) | ✅ Yes | Same object via copied address |
| `p = new Point(...)` (**reassign**) | ❌ No | Only the local copy of the address changes |
| `p = null` | ❌ No | Same reason |
| `str = str + "x"` (`String`) | ❌ No | `String` is immutable **and** you reassigned |
| `list.add(item)` / `arr[0] = 5` | ✅ Yes | Mutates the shared object |
| `arr = new int[5]` | ❌ No | Reassignment |

> **JS contrast:** identical semantics to JavaScript. Primitives are copied; for objects the *reference* is copied. Mutation is visible, reassigning the parameter isn't. Java simply has no escape hatch either.

> **Interview trap:** *"Primitives are pass-by-value and objects are pass-by-reference. True?"* **False.** Everything is pass-by-value. For objects, the value passed is the reference. Saying "both" is the classic fresher mistake (the video calls this out too).

**How to emulate "output parameters"** (Java has no `ref`/`out`): return a value or a small result object/`record`, or pass a holder such as a one-element array.



### 9. Reference Copy vs Shallow Copy vs Deep Copy

Three different things people call "copying":

```java
Point r1 = new Point(4, 5);
Point r3 = r1;              // (A) reference copy: NO new object
Point r2 = new Point(r1);   // (B) copy constructor: NEW object, fields copied
```

```java
Point(Point p) { this.x = p.x; this.y = p.y; }   // copy constructor
// Accessing p.x is legal even if x is private: access is class-scoped, not object-scoped
```

```
(A) r3 = r1                          (B) r2 = new Point(r1)

STACK        HEAP                    STACK        HEAP
r1 ──┐   ┌────────────┐              r1 ───►  ┌────────────┐
     ├──►│ Point 4,5  │                        │ Point 4,5  │
r3 ──┘   └────────────┘              r2 ───►  ┌────────────┐
                                               │ Point 4,5  │  (independent)
r1 == r3  ->  true                             └────────────┘
change via r3 shows in r1            r1 == r2  ->  false
                                     change via r2 does NOT show in r1
```

> **⚠ Correction (terminology):** the video calls `r2 = new Point(r1)` a **deep copy** and `r3 = r1` a **shallow copy**. That is not the standard meaning:
> - `r3 = r1` is **not a copy of the object at all**. It's an **alias / reference copy**.
> - `new Point(r1)` creates a **new object**. Whether it is *shallow* or *deep* depends on **how it treats reference fields**, and `Point` has none (only `int`s), so the distinction doesn't even arise yet.

#### The real definition

| Copy type | New outer object? | Nested objects (reference fields) |
|---|---|---|
| **Alias** (`b = a`) | ❌ No | Everything shared |
| **Shallow copy** | ✅ Yes | **Shared** (only the reference values are copied) |
| **Deep copy** | ✅ Yes | **Recursively cloned** (nothing mutable shared) |

```java
class Address { String city; Address(String c) { city = c; } }

class Person {
    String  name;
    Address address;
    Person(String name, Address address) { this.name = name; this.address = address; }

    // ❌ SHALLOW: new Person, but SAME Address object
    static Person shallowCopy(Person p) { return new Person(p.name, p.address); }

    // ✅ DEEP: new Person AND new Address
    static Person deepCopy(Person p)    { return new Person(p.name, new Address(p.address.city)); }
}

Person orig    = new Person("Aditya", new Address("Guwahati"));
Person shallow = Person.shallowCopy(orig);
Person deep    = Person.deepCopy(orig);

orig.address.city = "Mumbai";
System.out.println(shallow.address.city);   // Mumbai     <- leaked through shared Address
System.out.println(deep.address.city);      // Guwahati   <- independent
```

```
shallow copy:                          deep copy:

orig ──► Person ──┐                    orig ──► Person ──► Address("Mumbai")
                  ├──► Address         deep ──► Person ──► Address("Guwahati")
shallow ► Person ─┘    (shared!)
```

> **Rule of thumb:** a shallow copy is **enough** when every field is a primitive or an **immutable** object (`String`, `Integer`, `LocalDate`, records of immutables). You need a deep copy only for **mutable** nested objects.

#### How to copy in real code

| Approach | Notes |
|---|---|
| **Copy constructor / static factory** | Preferred. Explicit, type-safe, works with `final` fields |
| `clone()` + `Cloneable` | Avoid. Marker interface with no methods, `Object.clone()` is protected, **shallow by default**, bypasses constructors, awkward with `final` fields |
| Serialization round-trip | Deep copy for free but slow, requires `Serializable` |
| `array.clone()`, `Arrays.copyOf`, `new ArrayList<>(list)`, `List.copyOf` | All **shallow**. Elements are shared |
| Immutable design (`final` fields, records) | Best: nothing to defensively copy |

**Defensive copying (production habit):**
```java
// ❌ WRONG — leaks internal mutable state, caller can mutate it
public Date getCreated() { return created; }
public Person(List<String> tags) { this.tags = tags; }

// ✅ RIGHT
public Date getCreated() { return new Date(created.getTime()); }
public Person(List<String> tags) { this.tags = List.copyOf(tags); }   // immutable snapshot
```



### 10. Full Working Code Example

```java
class Point {
    int x, y;
    Point(int x, int y) { this.x = x; this.y = y; }
    Point(Point p)      { this(p.x, p.y); }               // copy constructor
}

class Address {
    String city;
    Address(String city) { this.city = city; }
}

class Person {
    String  name;
    Address address;
    Person(String name, Address address) { this.name = name; this.address = address; }
    static Person shallowCopy(Person p) { return new Person(p.name, p.address); }
    static Person deepCopy(Person p)    { return new Person(p.name, new Address(p.address.city)); }
}

public class Demo {
    static void addTen(int x, int y)   { x += 10; y += 10; }
    static void addTen(Point p)        { p.x += 10; p.y += 10; }
    static void reassign(Point p)      { p = new Point(99, 99); p.x += 1; }
    static void swap(Point a, Point b) { Point t = a; a = b; b = t; }

    public static void main(String[] args) {
        // 1. primitives: value copied
        int x = 4, y = 5;
        addTen(x, y);
        System.out.println(x + " " + y);                 // 4 5

        // 2. object: reference copied, object mutated
        Point p1 = new Point(4, 5);
        addTen(p1);
        System.out.println(p1.x + " " + p1.y);           // 14 15

        // 3. reassigning the parameter does nothing to the caller
        reassign(p1);
        System.out.println(p1.x + " " + p1.y);           // 14 15

        // 4. swap does not work
        Point p2 = new Point(1, 2), p3 = new Point(3, 4);
        swap(p2, p3);
        System.out.println(p2.x + " " + p3.x);           // 1 3

        // 5. alias vs copy constructor
        Point p4 = p1;                // alias
        Point p5 = new Point(p1);     // independent copy
        p4.x = 100;
        System.out.println(p1.x);                        // 100
        System.out.println(p5.x);                        // 14
        System.out.println(p1 == p4);                    // true
        System.out.println(p1 == p5);                    // false

        // 6. shallow vs deep
        Person orig    = new Person("Aditya", new Address("Guwahati"));
        Person shallow = Person.shallowCopy(orig);
        Person deep    = Person.deepCopy(orig);
        orig.address.city = "Mumbai";
        System.out.println(shallow.address.city);        // Mumbai
        System.out.println(deep.address.city);           // Guwahati
    }
}
```

**Output:**
```
4 5
14 15
14 15
1 3
100
14
true
false
Mumbai
Guwahati
```

> **Naming tip:** the video names its class `Random`, which clashes with `java.util.Random` in real projects. Use domain names (`Point`, `Coordinates`).



### 11. Key Interview Points

| Question | Answer |
|---|---|
| Size of a reference? | 4 bytes with compressed oops (default, heap < ~32 GB), else 8 bytes |
| What is in an object header? | Mark word (8 B: hash, GC age, lock state) + class pointer (4 B, to Metaspace class metadata). Arrays add a 4 B length |
| Size of `new Object()`? | 12 B header -> padded to **16 B** |
| Why are objects padded? | 8-byte alignment (hardware-friendly, and enables compressed oops to reach 32 GB) |
| Why can a 40 GB heap use more memory than a 30 GB heap for the same data? | Above ~32 GB compressed oops turn off: every reference doubles to 8 B |
| Does an object's size include what its references point to? | No. That is **shallow size**. Retained size includes exclusively-owned referents |
| Is Java pass-by-value or pass-by-reference? | **Always pass-by-value.** For objects, the *reference* is copied |
| Prove Java isn't pass-by-reference | A `swap(a, b)` of two objects doesn't swap anything |
| Why does mutating a passed object affect the caller? | Both references point to the same heap object |
| Why doesn't reassigning the parameter affect the caller? | You only changed your local copy of the address |
| `b = a` copies the object? | No: aliasing. Both refer to one object (`a == b` is `true`) |
| Shallow vs deep copy? | Both make a new outer object. Shallow shares nested objects, deep clones them |
| When is a shallow copy enough? | All fields primitive or immutable |
| Why avoid `clone()`? | Shallow by default, `Cloneable` is a broken marker interface, bypasses constructors, clashes with `final` |
| Are `Arrays.copyOf` / `new ArrayList<>(list)` deep copies? | No, shallow. Elements are shared |
| Where do reference variables live? | Locals/params: stack frame. Reference *fields*: inside the heap object |
| Does GC remove the parameter reference when a method returns? | No. The frame pops. GC only reclaims unreachable *objects* |



### 12. Quick Summary / Mental Model

```
OBJECT SIZE
───────────
size = align8( 12 (header) + fields )        arrays: 16 header + elements
        └ mark word 8 + class pointer 4        refs: 4 B (compressed) / 8 B

METHOD CALLS (always by VALUE)
──────────────────────────────
primitive   ─► copy of the value             callee change  ✗ invisible
reference   ─► copy of the ADDRESS           mutate object  ✓ visible
                                             reassign param ✗ invisible

COPYING
───────
b = a                      alias (same object)
new T(a) / factory         NEW object
   nested refs shared   ─► shallow
   nested refs cloned   ─► deep
```



### 13. Checklist & Golden Rules

#### Checklist
- [ ] Reference = 4 B (compressed oops) or 8 B; object size = `align8(12 + fields)`.
- [ ] Header = mark word (8) + class pointer (4); class pointer targets Metaspace metadata, not the object.
- [ ] `Person { byte age; }` = **16 B** (the video's 18 is wrong).
- [ ] Sizes are **shallow**; use MAT/heap dumps for retained size, JOL for layout.
- [ ] Java is **always** pass-by-value; references pass their address by value.
- [ ] Mutation through a parameter is visible; reassignment is not.
- [ ] `b = a` is an alias, not a copy; `==` compares addresses.
- [ ] Shallow copy shares nested objects; deep copy clones them.
- [ ] `clone()`, `Arrays.copyOf`, `new ArrayList<>(x)` are shallow.

#### Golden Rules
1. **Never say "Java passes objects by reference".** Say "passes the reference by value".
2. **Don't reassign parameters** expecting the caller to see it; return the new value instead.
3. **Prefer immutability** (`final` fields, records, `List.copyOf`) so copying questions disappear.
4. **Copy defensively** at API boundaries (constructors/getters) when holding mutable state.
5. **Prefer copy constructors / static factories over `clone()`.**
6. **Think in boxed vs primitive cost:** `List<Integer>` costs ~5x `int[]` per element (4 B ref + 16 B `Integer` vs 4 B).
7. **Measure, don't guess:** use JOL and heap dumps when memory matters.




## 15. Java `static` and `final` Keywords



### 0. Corrections to the video (read first)

The video simplifies a few things. These are the accurate versions.

| Video says | Accurate version |
|---|---|
| "Static variables are not stored in Heap" | Conceptually they belong to the **class**, not any object. In HotSpot (Java 8+), class **metadata and method bytecode** live in **Metaspace**, while the **values of static fields are held in the `java.lang.Class` object, which is on the Heap**. Safe interview answer: "one copy per class, not per object, and not inside instance memory." |
| "A static method can only call other static methods" | It cannot call an **instance** method *without an object*. It can call one through an object reference: `new Student().print();` is legal inside a static method. Same for instance fields. |
| "Statics run as soon as the class is loaded" | They run at **class initialization**, which is lazy: triggered on first `new`, first static method call, or first access to a non-constant static field. *Loading* and *initialization* are different steps. |
| "`final` variable = constant" | Only true for primitives and immutable objects. A `final` **reference** cannot be re-pointed, but the **object it points to can still be mutated**. |
| "Java allows `final int x; x = 4;` as an optimization" | It is not an optimization. It is the **definite assignment** rule (JLS Ch. 16): a *blank final* must be assigned **exactly once** before use. |
| "Constants are UPPER_CASE for any `final`" | The convention applies to **constants**, i.e. `static final` fields with immutable values. A non-static `final` instance field is normally `camelCase`. |
| "A reference takes 4 bytes" | 4 bytes only with **compressed oops** (default when heap is below ~32 GB). Otherwise 8 bytes. |

---

### 1. The problem `static` solves

```java
class Student {
    String name;
    int    age;
    int    rollNumber;
    String college;   // same for every student of this application
}
```

If the app models one college (say "IIT Guwahati"), every `Student` object stores its own copy of `college` with an identical value. With 100,000 objects that is 100,000 redundant references.

**Fix:** make it a **class-level** member. One copy exists, shared by all instances.

```java
class Student {
    String name;
    int    age;
    int    rollNumber;
    static String college = "IIT Guwahati";   // one copy for the whole class
}
```

```
HEAP (per-object)                       CLASS-LEVEL (one copy)
┌──────────────────────┐                ┌────────────────────────────┐
│ s1: name, age, roll  │ ─ shares ────► │ Student.college = "IIT G"  │
├──────────────────────┤                │ methods (bytecode)         │
│ s2: name, age, roll  │ ─ shares ────► │ (Metaspace / Class object) │
└──────────────────────┘                └────────────────────────────┘
```

> **Rule of thumb:** if a value belongs to the *concept* (the class) rather than to an *individual*, it is `static`.



### 2. Static variables (class variables)

| | Instance variable | Static variable (class variable) |
|---|---|---|
| Belongs to | Each object | The class |
| Copies | One per object | Exactly one per class (per ClassLoader) |
| Created | On `new` | On class initialization |
| Access | `obj.field` | `ClassName.field` (preferred) |
| Change by one object | Affects only that object | **Visible to all objects** |

```java
Student s1 = new Student();
Student s2 = new Student();

s1.college = "IIT Kharagpur";           // legal, but misleading (compiler warns)
System.out.println(s2.college);         // IIT Kharagpur, changed for everyone
System.out.println(Student.college);    // IIT Kharagpur  <- preferred access style
```

**Why the compiler warns on `s1.college`:** "The static field should be accessed in a static way." The instance is irrelevant, only its *declared type* matters. That is misleading to readers.

```java
Student s = null;
System.out.println(s.college);   // NO NullPointerException. Resolved at compile time via the type Student.
```

#### Real use: shared counter (auto roll number)

```java
class Student {
    private static int nextRoll = 1000;      // shared state
    final int rollNumber;                    // per-object, assigned once

    Student() { this.rollNumber = ++nextRoll; }
}
```

This is **not thread-safe**. Two threads can read the same `nextRoll`. Use `AtomicInteger`:

```java
private static final AtomicInteger NEXT_ROLL = new AtomicInteger(1000);
Student() { this.rollNumber = NEXT_ROLL.incrementAndGet(); }
```



### 3. Static methods

```java
class Student {
    static String college = "IIT Guwahati";

    static void printCollege() {           // no object needed
        System.out.println(college);
    }
}

Student.printCollege();   // preferred
new Student().printCollege();  // legal, discouraged
```

Neither static nor non-static methods are stored inside objects. Each object holds only its **instance fields plus a header** (mark word and class pointer). Method bytecode exists **once per class** and is shared.

#### The 3 rules (with the precise reasoning)

| # | Rule | Why | Precise statement |
|---|---|---|---|
| 1 | Static method cannot call an **instance method** directly | Instance methods need a receiver object; a static method has none. The JVM cannot know *which* object's `print()` you mean. | Allowed through an explicit reference: `obj.print()` |
| 2 | Static method cannot use **instance variables** directly | Instance fields exist only after `new`. `Student.markAttendance()` needs no object. | Allowed through a reference: `obj.name` |
| 3 | Static method has no **`this`** (or `super`) | `this` = reference to the current object. A static context has no current object. | Compile error if used |

```java
class Student {
    String name = "Aditya";

    void print() { System.out.println(name); }

    static void markAttendance() {
        // print();              // ERROR: cannot make a static reference to a non-static method
        // System.out.println(name); // ERROR
        // System.out.println(this); // ERROR

        Student s = new Student();   // OK: explicit object
        s.print();
    }
}
```

The reverse is always allowed: **instance methods can freely use static members.**

#### Beyond the video: static methods are not polymorphic

Static methods are **hidden**, not overridden. Resolution uses the **declared (compile-time) type** (static binding), not the runtime object.

```java
class Parent { static void hello() { System.out.println("Parent"); } }
class Child extends Parent { static void hello() { System.out.println("Child"); } }

Parent p = new Child();
p.hello();          // prints "Parent". No runtime dispatch.
```

Consequences:
- `@Override` on a static method is a compile error.
- A static method cannot be `abstract`.
- Static methods cannot be overridden by an instance method (compile error) and vice versa.

#### Beyond the video: `static synchronized`

```java
static synchronized void m() { }   // locks on Student.class, not on any instance
synchronized void n() { }          // locks on `this`
```

They use **different monitors**, so a static synchronized and instance synchronized method can run concurrently.

---

### 4. Static block (static initializer)

Initializes static variables when logic is needed. It is the static counterpart of a constructor.

```java
class Student {
    static String college;
    static int grade;

    static {                       // runs ONCE, at class initialization
        college = "IIT Guwahati";
        grade   = 8;
    }
}
```

- Multiple static blocks are allowed. They run in **textual order**, interleaved with static field initializers.
- A static block **cannot throw checked exceptions**.
- If it throws an unchecked exception, the JVM throws `ExceptionInInitializerError`. Every later use of that class throws `NoClassDefFoundError`, and the class is unusable in that ClassLoader.
- **Forward reference rule:** you may *assign* a static field before its declaration, but you may not *read* it by simple name.

```java
static {
    x = 10;                        // OK: assignment
    // System.out.println(x);      // ERROR: illegal forward reference
}
static int x = 5;                  // note: x ends up 5, not 10, because textual order wins
```

#### Beyond the video: complete initialization order

```java
class Parent {
    static { System.out.println("1. Parent static block"); }
    { System.out.println("3. Parent instance block"); }
    Parent() { System.out.println("4. Parent constructor"); }
}
class Child extends Parent {
    static { System.out.println("2. Child static block"); }
    { System.out.println("5. Child instance block"); }
    Child() { System.out.println("6. Child constructor"); }
}

new Child();
new Child();   // static blocks do NOT run again
```

```
1. Parent static block
2. Child static block
3. Parent instance block
4. Parent constructor
5. Child instance block
6. Child constructor
3. Parent instance block      <- second object: only steps 3-6 repeat
4. Parent constructor
5. Child instance block
6. Child constructor
```

**Order:** parent static → child static (once per class) → for each object: parent instance init → parent ctor → child instance init → child ctor.

#### Beyond the video: class loading vs initialization

| Phase | What happens |
|---|---|
| **Loading** | ClassLoader reads the `.class` file, creates the `Class` object and metadata |
| **Linking** | Verify bytecode → Prepare (static fields get **default values**: 0, null) → Resolve |
| **Initialization** | Static initializers and static blocks run (in textual order), and the assigned values replace the defaults |

Initialization is triggered by: `new`, calling a static method, reading/writing a non-constant static field, `Class.forName("X")`, or initializing a subclass (parent first).

It is **not** triggered by: referencing a **compile-time constant**, declaring a variable of the type, or accessing an array of the type.

```java
class A {
    static final int C = 10;          // compile-time constant, inlined by javac
    static final Integer D = 10;      // not a constant expression
    static { System.out.println("A initialized"); }
}

System.out.println(A.C);   // prints 10 only, A is NOT initialized
System.out.println(A.D);   // prints "A initialized" then 10
```

---

### 5. What can and cannot be `static`

| Element | `static` allowed? | Reason |
|---|---|---|
| Field | Yes | Class-level state |
| Method | Yes | Class-level behaviour |
| Initializer block | Yes | Class-level initialization |
| **Nested class** | Yes | Does not need an outer instance |
| **Top-level class** | **No** | A top-level class is not a member of anything; `static` has no meaning |
| **Method parameter** | **No** | Parameters are local variables on the stack; they die with the call |
| **Local variable** | **No** | Same reason. No class-level meaning. (In C, function-static locals exist; in Java they do not.) |
| Constructor | **No** | Constructors initialize an *instance* |

#### Static nested vs inner class

```java
class Outer {
    private int x = 1;
    static class Nested { }       // no hidden reference to an Outer instance
    class Inner { int get() { return x; } }  // holds an implicit Outer.this reference
}

Outer.Nested n = new Outer.Nested();          // no Outer needed
Outer.Inner  i = new Outer().new Inner();     // needs an Outer instance
```

**Why it matters:** an inner class keeps a hidden reference to its outer object. This is a classic **memory leak** (an inner class instance or listener outlives the outer object and prevents its GC). Prefer `static` nested classes unless you need the outer instance. Builders, `Map.Entry` implementations, and holders are almost always `static`.

**Interface members** (fields, nested types) are implicitly `static`. Interfaces may also declare `static` methods (Java 8+), which are **not inherited** by implementing classes and must be called as `InterfaceName.method()`.

---

### 6. Why `main` is `static`

```java
public static void main(String[] args) { }
```

The JVM launcher loads your class, finds `main`, and invokes it. If `main` were an instance method, the JVM would first have to construct an object of your class, but it does not know which constructor to call or which arguments to pass. Making `main` static removes the need for an instance. The JVM effectively calls `Demo.main(args)`.

Consequences:
- Every method called **directly** from `main` must also be static (or be called on an object you create).
- Error you will see: `Cannot make a static reference to the non-static method`.

| Part | Meaning |
|---|---|
| `public` | JVM (outside your package) must be able to call it |
| `static` | Callable without an instance |
| `void` | JVM ignores any return value. Exit code is set via `System.exit()` |
| `String[] args` | Command-line arguments |

**Beyond the video:**
- The parameter can be `String... args` or any name, e.g. `String[] a`.
- `final String[] args` is legal.
- Overloads of `main` are legal, but only the exact signature is the entry point.
- **Newer Java:** instance `main` and simplified entry points were introduced as preview features in Java 21-24 and finalized in **Java 25 (JEP 512)**. Classic `public static void main(String[] args)` still works everywhere and is what you will see in almost all production code.

---

### 7. `static` in real projects: patterns and pitfalls

| Pattern | Example | Note |
|---|---|---|
| Constants | `public static final int MAX_RETRIES = 3;` | Most common use |
| Utility class | `Math`, `Collections`, `Objects` | Make it `final` with a **private constructor** |
| Static factory | `List.of(...)`, `Integer.valueOf(...)` | Named, can cache, can return subtypes |
| Singleton holder | See below | Lazy and thread-safe without `synchronized` |
| Counters / registries | `AtomicInteger`, `ConcurrentHashMap` | Must be thread-safe |

```java
final class StringUtil {
    private StringUtil() { }                      // no instances
    static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
```

**Lazy singleton via static nested holder** (relies on the class-initialization guarantee: the JVM initializes a class once, thread-safely):

```java
class Config {
    private Config() { }
    private static class Holder { static final Config INSTANCE = new Config(); }
    static Config getInstance() { return Holder.INSTANCE; }  // Holder initializes on first call
}
```

**Pitfalls:**

| Pitfall | Why it hurts |
|---|---|
| **Mutable static state** | Hidden global state. Thread-safety bugs, test pollution (one test changes state seen by the next). |
| **Static collections that only grow** | Objects stay reachable for the life of the ClassLoader, so they never get GC'd → memory leak. |
| **Static methods for business logic with dependencies** | Cannot be mocked easily, cannot use dependency injection. In Spring code, prefer beans. |
| **Static state in web/app servers** | Each ClassLoader gets its **own copy** of statics (e.g. per-webapp), so "one per JVM" is not guaranteed. |
| **Heavy work in static initializers** | Slows class loading, and failures produce `NoClassDefFoundError`, which is hard to debug. |

---

### 8. The `final` keyword

`final` means "**cannot be changed after it is set**". The meaning depends on where you place it.

| Applied to | Effect |
|---|---|
| **Variable** | Can be assigned only once |
| **Method parameter** | Cannot be reassigned inside the method |
| **Method** | Cannot be **overridden** in subclasses |
| **Class** | Cannot be **extended** |

#### 8.1 `final` variables

```java
class Circle {
    final double PI = 3.14;      // assigned at declaration
}
Circle c = new Circle();
// c.PI = 3.16;   // ERROR: cannot assign a value to final variable PI
```

**Three legal places to assign a final instance field** (exactly once on every path):
1. At the declaration.
2. In an **instance initializer block**.
3. In **every constructor** (or delegated through `this(...)`).

```java
class Circle {
    private final double radius;                 // blank final
    Circle()               { this(1.0); }        // delegates, so radius is assigned via this(...)
    Circle(double radius)  { this.radius = radius; }
}
```

A **blank final** that a constructor path leaves unassigned is a compile error: `variable radius might not have been initialized`.

**Static final** fields are assigned at the declaration or in a **static block** (a constructor is not allowed, because a constructor runs per instance).

```java
static final double PI;
static { PI = 3.14; }         // OK
```

**Local final variables** follow definite assignment:

```java
final int x;
x = 4;          // OK: first assignment
// x = 5;       // ERROR: variable x might already have been assigned
```

#### 8.2 Key gotcha: `final` reference ≠ immutable object

```java
final List<String> names = new ArrayList<>();
names.add("Aditya");                 // OK: the object is mutated
// names = new ArrayList<>();        // ERROR: the reference cannot change

final int[] arr = {1, 2, 3};
arr[0] = 99;                         // OK: array elements are mutable
```

`final` protects the **variable**. For a truly immutable value, the **object's class** must be immutable (`String`, `LocalDate`, `List.of(...)`, records with immutable components).

#### 8.3 `final` parameters and locals

```java
void print(final String name) {
    // name = "x";     // ERROR
}
```

Also relevant: lambdas and anonymous/inner classes can capture only **final or effectively final** locals (never reassigned after initialization). You do not have to write `final` for this.

```java
int count = 0;
Runnable r = () -> System.out.println(count);
// count++;   // would make `count` non-effectively-final, so the lambda line above fails to compile
```

#### 8.4 `final` methods and classes

```java
class Payment {
    final void audit() { /* must not be altered by subclasses */ }
}
final class Money { }               // cannot be extended
```

| Use | Examples / reasoning |
|---|---|
| `final class` | `String`, `Integer`, `Math`. Protects immutability and security. Subclassing `String` would let you break its guarantees. |
| `final` method | Template method pattern: lock the algorithm skeleton, allow overriding of only the hooks. |

**Note:** private and static methods cannot be overridden anyway, so making them `final` is redundant. `final` does **not** meaningfully help the JIT. HotSpot uses class hierarchy analysis and inlines regardless. Do not add `final` for performance.

#### 8.5 Beyond the video: `final` and the Java Memory Model

`final` fields have a special guarantee (JLS §17.5): once a constructor finishes, **any thread that sees the object reference is guaranteed to see the correct values of its final fields**, even without synchronization. This is why immutable classes are inherently thread-safe.

The condition is that the constructor must not leak `this`:

```java
class Bad {
    final int v;
    Bad(Registry r) { r.register(this); this.v = 42; }   // `this` escapes before v is set → unsafe
}
```

#### 8.6 Beyond the video: compile-time constants are inlined

```java
public static final int TIMEOUT = 30;            // constant variable: javac copies 30 into callers
public static final Integer TIMEOUT2 = 30;       // NOT inlined (not a constant expression)
```

If you change `TIMEOUT` in library A but do not **recompile** dependent class B, B keeps using the old value. This is a known pitfall with shared constants. Use non-constant initialization if the value may change between releases.

#### 8.7 Beyond the video: can `final` be bypassed?

Reflection with `setAccessible(true)` can modify a *final instance field* in some cases, and the JVM may keep serving the old value because of inlining and caching. Static final fields and record fields are protected, and newer Java versions are restricting this further. Treat `final` as a hard guarantee in your code.

---

### 9. `static final`: constants

```java
class Circle {
    public static final double PI = 3.14159;   // shared by all, and never changes
    public static final int MAX_RADIUS;
    static { MAX_RADIUS = 1000; }
}
```

- `static` → one copy for the whole class (saves memory, accessible as `Circle.PI`).
- `final` → value cannot change.
- **Naming:** `UPPER_SNAKE_CASE` with underscores between words: `MAX_RETRY_COUNT`, `DEFAULT_TIMEOUT_MS`.

**Best practices:**

| Do | Avoid |
|---|---|
| `public static final String` / primitives | `public static final List<String> X = new ArrayList<>();` (contents are mutable). Use `List.of(...)` or `Collections.unmodifiableList(...)`. |
| Use an **`enum`** for a fixed set of related constants | Constants-only **interfaces** (constant interface antipattern) |
| Group constants in a `final` class with a private constructor | Magic numbers scattered in code |

---

### 10. `String[] args`: command-line arguments

`args` holds the arguments typed **after the class name** when launching the program. Every element is a `String`.

```java
public class Demo5 {
    public static void main(String[] args) {
        System.out.println("Number of arguments: " + args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.println("Argument " + i + " = " + args[i]);
        }
    }
}
```

```bash
javac Demo5.java
java Demo5                          # Number of arguments: 0
java Demo5 Aditya Tandon Rohit Negi
# Number of arguments: 4
# Argument 0 = Aditya   ... Argument 3 = Negi
```

Since Java 11 you can skip the compile step for a single file: `java Demo5.java Aditya Tandon`.

**Details:**
- `args` is **never `null`**. With no arguments it is an empty array (`length == 0`).
- Space separates arguments. Use quotes for a single argument containing spaces: `java Demo5 "Aditya Tandon"`.
- Arguments are always `String`. Convert with `Integer.parseInt(args[0])`, which can throw `NumberFormatException`.
- `args[0]` with no arguments throws `ArrayIndexOutOfBoundsException`. Always check `args.length` first.

**JVM options vs program arguments** (order matters):

```bash
java -Xmx512m -Denv=prod  -jar app.jar   --server.port=9090
#    └──── JVM options ────┘        └── program args (in args[]) ──┘
```

**Where this is used today:** Spring Boot (`--server.port=9090`, `--spring.profiles.active=prod`), CLI tools (often via libraries like picocli), batch jobs, and container `ENTRYPOINT`/`CMD` arguments. Configuration passed on the command line reaches your code through `args`.

---

### 11. Full example: everything together

```java
public class Student {

    // ---- class-level (static) ----
    static final String COLLEGE;                              // static final, blank, set in static block
    private static final AtomicInteger NEXT_ROLL = new AtomicInteger(1000);

    static {
        COLLEGE = "IIT Guwahati";
        System.out.println("Student class initialized");     // runs once
    }

    // ---- per-object ----
    private final String name;                                // set once in the constructor
    private final int    rollNumber;
    private int          age;                                 // mutable

    Student(String name, int age) {
        this.name       = name;
        this.age        = age;
        this.rollNumber = NEXT_ROLL.incrementAndGet();
    }

    static String getCollege() { return COLLEGE; }            // static method, uses only static state

    void print() {                                            // instance method, can use both kinds
        System.out.println(name + ", " + age + ", " + rollNumber + ", " + COLLEGE);
    }

    public static void main(String[] args) {
        Student s1 = new Student("Aditya", 28);
        Student s2 = new Student("Rohit", 28);
        s1.print();      // Aditya, 28, 1001, IIT Guwahati
        s2.print();      // Rohit, 28, 1002, IIT Guwahati
        System.out.println(Student.getCollege());
    }
}
```

Output:

```
Student class initialized
Aditya, 28, 1001, IIT Guwahati
Rohit, 28, 1002, IIT Guwahati
IIT Guwahati
```

---

### 12. Quick comparison

| | `static` | `final` |
|---|---|---|
| Question it answers | **Whose** is it? (class vs object) | **Can it change?** |
| Variable | One shared copy | Assigned once |
| Method | Called without an object; hidden, not overridden | Cannot be overridden |
| Class | Only for nested classes | Cannot be extended |
| Block | Static initializer, runs once | (not applicable) |
| Combined | `static final` = shared constant | |

---

### 13. Interview questions

| Question | Answer |
|---|---|
| What is a `static` variable? | A class-level variable: one copy per class (per ClassLoader), shared by all instances. |
| Where are static variables stored? | Class metadata is in **Metaspace**; in HotSpot the static field values are held in the `Class` object on the **Heap**. Not inside instance objects. |
| Can a static method access instance members? | Not directly (no `this`). Yes via an explicit object reference. |
| Can we override a static method? | No. It is **hidden**, resolved at compile time from the declared type. |
| Can a static method be abstract? | No. Abstract needs overriding; static cannot be overridden. |
| Can a constructor be static? | No. |
| When does a static block run? | Once, at class **initialization** (not merely loading), before any instance creation or static member use. |
| Can a class have multiple static blocks? | Yes; they run in textual order. |
| What if a static block throws? | `ExceptionInInitializerError`, and later uses of the class throw `NoClassDefFoundError`. |
| Why is `main` static? | So the JVM can call it without constructing an object of the class. |
| Can we run a program without `main`? | A class with no entry point cannot be launched with `java` (Java 7+ checks for `main` first). Frameworks, tests, and servlet containers call your code through other entry points. |
| Can `main` be overloaded / `final` / `synchronized`? | Yes to all. The JVM calls only the standard signature. |
| What is a top-level static class? | Not allowed. Only nested classes can be `static`. |
| Difference between static nested and inner class? | Static nested has no outer instance. Inner holds an implicit outer reference (leak risk). |
| Does `final` make an object immutable? | No. It only prevents reassigning the variable. |
| What is a blank final? | A final variable declared without initializer. It must be assigned exactly once (constructor, initializer block, or static block). |
| Can a final variable be assigned in a constructor? | Yes, for instance fields (each constructor path must assign it exactly once). |
| Why must lambda-captured variables be effectively final? | Locals live on the stack; the lambda captures a **copy**. Allowing mutation would make the copy and original disagree. |
| `final` vs `finally` vs `finalize()`? | `final`: modifier. `finally`: block that always runs after try/catch. `finalize()`: deprecated GC hook, do not use (use try-with-resources / `Cleaner`). |
| Why is `String` final? | Immutability → safe as `HashMap` keys, thread-safe, enables the string pool, protects security-sensitive uses (class names, file paths). |
| Is `static final int X = 5` inlined? | Yes. It is a compile-time constant, so dependents need recompiling if it changes. |
| Can we declare a static variable inside a method? | No. Locals cannot be `static` in Java. |
| Is static thread-safe? | No. Mutable statics need `volatile`, atomics, locks, or concurrent collections. |
| Does `static` affect memory? | Saves per-object copies, but the value lives until the class is unloaded, so large statics can leak. |
| Order of initialization for a child object? | Parent static → child static → parent instance init → parent ctor → child instance init → child ctor. |

---

### 14. Summary / mental model

```
static  →  "belongs to the CLASS"      final  →  "assigned ONCE"
           one copy, shared                       variable: no reassignment
           no `this`, no object needed            method:   no overriding
           runs at class initialization           class:    no subclassing

static final  →  shared constant   (UPPER_SNAKE_CASE)

main is static  →  JVM calls it without creating an object
String[] args   →  command-line arguments after the class name, always Strings
```

**Remember:**
1. `static` = class-level, `final` = single assignment. They are independent and combine freely.
2. Static methods have no `this`. They reach instance state only through an object reference.
3. Statics are hidden, not overridden, and resolved at compile time.
4. `final` reference ≠ immutable object.
5. Static initialization order: parent before child, textual order within a class, once per class.
6. Mutable static state is a design smell: prefer instance state, dependency injection, or thread-safe structures.

## 16. Pillars of OOP (Part 1): Encapsulation and Inheritance



### 0. Corrections to the video (read first)

| Video says | Accurate version |
|---|---|
| Encapsulation = `private` fields + getters/setters | That is the *mechanism*, not the goal. The goal is to **protect invariants**: an object should never be put into an invalid state from outside. Blind getters/setters for every field expose the data again and defeat the purpose. Prefer **behaviour methods** (`deposit`, `withdraw`) over `setBalance`. |
| "`default` is an access modifier keyword" | There is **no `default` keyword** for access. Writing nothing gives **package-private** access. (`default` exists only in `switch` and interface default methods.) |
| `super.super.rollNumber` | **Not valid Java.** You cannot chain `super`. You can only reach the *immediate* parent's members with `super.x`. |
| "`super` stores the reference of the parent object" | There is **no separate parent object**. A child instance is *one object* that contains the parent's fields plus its own. `super` is not a variable: you cannot assign it or pass it around (unlike `this`). It is a keyword that tells the compiler to look in the parent class. |
| "The compiler can't tell at runtime which method to call" (diamond problem) | It is a **language-design decision**: Java bans multiple inheritance of *classes* to avoid ambiguity of behaviour **and state**. Java *does* allow multiple inheritance of type (interfaces), and since Java 8 of behaviour (default methods), with an explicit conflict-resolution rule. |
| "Importing loads the class's bytecode" | `import` is only a **naming shortcut** resolved at compile time. Nothing is copied or loaded. Classes are loaded **lazily at runtime** by the ClassLoader when first used. |
| "A package is a folder" | A package is a **namespace**. It maps to a directory structure because the compiler and classpath expect that layout, but conceptually it groups related types and controls package-private access. |
| "Method in child shadows the parent's" | Methods are **overridden** (runtime dispatch). Fields are **hidden**, not overridden (resolved by reference type). Different mechanisms. |
| `double balance` for money | Never use `double`/`float` for money (binary floating-point cannot represent 0.1 exactly). Use `BigDecimal` or integer minor units (paise/cents). |



### 1. Encapsulation

**Definition (two parts):**
1. **Bundling:** data (fields) and behaviour (methods) live together in one unit (the class).
2. **Data hiding:** restrict direct access to the data; expose it only through a controlled public interface.

```
┌───────────── BankAccount ─────────────┐
│  private double balance   ← hidden    │
│                                       │
│  + deposit(amount)        ← controlled│
│  + withdraw(amount)       ← controlled│
│  + getBalance()           ← read-only │
└───────────────────────────────────────┘
```

#### Problem without encapsulation

```java
class BankAccount { double balance; }

BankAccount ba = new BankAccount();
ba.balance = 10_000_000;   // anyone can set any value: no rules, no audit
```

#### With encapsulation

```java
import java.math.BigDecimal;

public class BankAccount {
    private BigDecimal balance = BigDecimal.ZERO;          // hidden state

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0)
            throw new IllegalArgumentException("Deposit must be positive");
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0)
            throw new IllegalArgumentException("Withdrawal must be positive");
        if (amount.compareTo(balance) > 0)
            throw new IllegalStateException("Insufficient funds");
        balance = balance.subtract(amount);
    }

    public BigDecimal getBalance() { return balance; }     // getter only. No setBalance() on purpose.
}
```

**Key design point:** the class has **no `setBalance`**. The real world does not let you edit a balance directly; you only deposit or withdraw. Rules (validation) live in one place, the methods.

#### Getters and setters: what they are good for

```java
public class Student {
    private String name;
    private int age;
    private String college;

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name required");
        this.name = name.trim();
    }

    public void setAge(int age) {
        if (age < 0 || age > 120) throw new IllegalArgumentException("Invalid age");
        this.age = age;
    }
}
```

Benefits: **validation**, **read-only / write-only** fields (omit the setter or getter), **change internals later** without breaking callers (rename a field, compute a value lazily), **hook points** (logging, notifications, thread-safety).

#### Beyond the video: pitfalls

**1. A getter can leak internal state.** Returning a reference to a mutable object breaks encapsulation even with `private`:

```java
class Student {
    private final List<String> courses = new ArrayList<>();

    // BAD: caller can do student.getCourses().clear()
    public List<String> getCourses() { return courses; }

    // GOOD: defensive copy or unmodifiable view
    public List<String> getCoursesSafe() { return List.copyOf(courses); }
    // or: Collections.unmodifiableList(courses)  (a live read-only view, not a copy)
}
```

Same rule for constructors and setters: **copy mutable arguments on the way in** (`Date`, arrays, collections).

**2. "Tell, don't ask."** Instead of `if (acc.getBalance() >= x) acc.setBalance(acc.getBalance() - x)`, call `acc.withdraw(x)`. Logic that uses an object's data belongs inside that object.

**3. Encapsulation is not thread-safety.** Two threads calling `withdraw` concurrently can corrupt the balance. Guard with `synchronized`, locks, or atomics.

**4. Immutability is the strongest encapsulation.** `private final` fields, no setters, no leaking of mutable state. Modern shortcut (Java 16+): `record Point(int x, int y) {}` gives an immutable class with accessors, `equals`, `hashCode`, `toString`.

**5. Encapsulation vs abstraction:** encapsulation *hides data* (how the state is stored). Abstraction *hides complexity* (what the caller needs to know). Related, but different pillars.

**6. Frameworks:** Getters/setters follow the **JavaBeans convention** (`getX`, `isX` for boolean, `setX`), which frameworks (Spring, Jackson, Hibernate) use via reflection. Tools like **Lombok** (`@Getter`, `@Setter`) generate them, but the design advice above still applies.



### 2. Access modifiers

Four levels of access, applicable to **fields, methods, constructors, and classes** (with restrictions on classes).

| Modifier | Same class | Same package | Subclass (different package) | Everywhere else |
|---|:---:|:---:|:---:|:---:|
| `private` | Yes | No | No | No |
| *(none)*: package-private | Yes | Yes | No | No |
| `protected` | Yes | Yes | Yes (via inheritance) | No |
| `public` | Yes | Yes | Yes | Yes |

Strictness order: **`private` > package-private > `protected` > `public`**.

```java
public class Demo {
    private   int a;   // this class only
              int b;   // package-private (no keyword)
    protected int c;   // same package + subclasses
    public    int d;   // everyone
}
```

#### Rules for classes (top-level)

| Modifier on a **top-level class** | Allowed? | Reason |
|---|---|---|
| `public` | Yes | Visible everywhere |
| package-private (none) | Yes | Visible in its package only |
| `private` | **No** | "Private to what?" There is no enclosing class. |
| `protected` | **No** | Subclass access is a member-level concept |

Nested classes can be `private` or `protected`, because they are *members* of the outer class.

**One `public` top-level class per file, and the file name must match it** (`Student.java` → `public class Student`). Non-public classes can share a file, but this is bad practice.

#### Beyond the video: precise details

**1. `private` is per class, not per object.** Another instance of the same class can access your private fields:

```java
class Account {
    private int balance;
    boolean richerThan(Account other) { return this.balance > other.balance; }  // legal
}
```

**2. Nested/inner classes can access the outer class's private members** (and vice versa). The compiler generates accessors internally (nestmates since Java 11).

**3. `protected` across packages has a subtle rule.** A subclass in a different package can access a protected member only **through a reference of its own type (or a subtype)**, not through a parent-type reference:

```java
package a;
public class Parent { protected int x; protected void hi() { } }
```
```java
package b;
import a.Parent;

public class Child extends Parent {
    void test() {
        x = 1;                    // OK (this.x)
        hi();                     // OK
        Child c = new Child();
        c.x = 2;                  // OK: reference of type Child
        Parent p = new Parent();
        // p.x = 3;               // ERROR: protected access through a Parent reference
    }
}
```

**4. Overriding cannot reduce visibility.** A `public` method in the parent cannot be overridden as `protected` or package-private in the child (compile error). Interface methods are implicitly `public`, so implementations must be `public`.

**5. Constructors:** a `private` constructor prevents outside instantiation (utility classes, singletons, static factory pattern). A package-private constructor allows creation only inside the package.

**6. Best practices:**
- Default to the **most restrictive** modifier that works: fields `private`, methods `public` only if part of the API.
- `public` + `private` cover almost everything in practice. `protected` appears in framework/base-class design. Package-private is useful for internal helpers and testing within the same package.
- Java 9+ **modules** add another layer: a `public` class is accessible outside its module only if the package is `exports`ed.

**7. Reflection can bypass `private`** via `setAccessible(true)` (subject to module restrictions). Access modifiers are compile-time and design tools, not a security boundary.



### 3. Packages

A **package** groups related classes/interfaces into a **namespace**. Purposes: avoid name clashes (`school.Student` vs `college.Student`), organize code, control access (package-private).

```
src/
└── com/example/college/
    ├── Student.java     →  package com.example.college;
    └── Teacher.java     →  package com.example.college;
```

```java
package com.example.college;          // must be the first statement (before imports)

public class Student {
    public void print() { System.out.println("College student"); }
}
```

**Naming convention:** all lowercase, **reverse domain name**: `com.company.project.module` (e.g. `org.springframework.boot`). This makes names globally unique.

#### Using classes from another package

```java
import com.example.college.Student;         // single-type import
import com.example.college.*;               // on-demand import (all types in that package)

public class Demo2 {
    public static void main(String[] args) {
        Student s = new Student();
        s.print();                           // print() must be public if Demo2 is in another package
    }
}
```

**Same package:** no import needed, and package-private members are visible (this is why the video's `Teacher` could call `Student.print()` with no modifier).

#### Name clashes: fully qualified names

```java
com.example.college.Student a = new com.example.college.Student();
com.example.school.Student  b = new com.example.school.Student();
```

Importing two classes with the same simple name causes a compile error (`... collides with another import`). Import one, and use the fully qualified name for the other.

#### Built-in packages

| Package | Contents |
|---|---|
| `java.lang` | `String`, `Object`, `Math`, `System`, wrappers. **Imported automatically.** |
| `java.util` | Collections (`ArrayList`, `HashMap`), `Optional`, `Scanner` |
| `java.io` / `java.nio` | Input/output and files |
| `java.time` | Modern date/time API |
| `java.util.concurrent` | Threads, executors, concurrent collections |

#### Beyond the video

**1. `import` is not `#include`.** It only lets you write `ArrayList` instead of `java.util.ArrayList`. No code is copied, no runtime cost, and no class is loaded by the import itself.

**2. `*` does not import sub-packages.** `import java.util.*;` does **not** import `java.util.concurrent.ConcurrentHashMap`.

**3. Resolution order:** single-type import beats on-demand (`*`) import; classes in the same package beat both.

**4. Static imports:** `import static java.lang.Math.*;` lets you write `sqrt(x)` (use sparingly; it hurts readability).

**5. The default package** (no `package` statement) works for toy programs, but classes in it **cannot be imported** from named packages. Never use it in real projects.

**6. Compiling and running with packages:**

```bash
javac -d out src/com/example/college/*.java src/com/example/Main.java
java  -cp out com.example.Main          # run using the fully qualified class name
```

In IDEs/Maven/Gradle the tooling handles this, but the directory must match the package (`com/example/college/`).

**7. Classpath and JARs:** packages are what JARs are made of. The **ClassLoader** finds `com/example/college/Student.class` on the classpath/modulepath by converting the package name to a path.



### 4. Inheritance

**Inheritance** lets a class (**subclass / child**) acquire the fields and methods of another (**superclass / parent**), modelling an **IS-A** relationship.

```java
class Vehicle {
    void start() { System.out.println("Vehicle starting"); }
}

class Car extends Vehicle {                 // Car IS-A Vehicle
    void setGear(int g) { System.out.println("Gear " + g); }
}

Car c = new Car();
c.start();       // inherited
c.setGear(2);    // own
```

- Every car is a vehicle, but not every vehicle is a car (reverse is false).
- Common code goes in the parent, specific code in the child.

#### Benefits

| Benefit | Explanation |
|---|---|
| **Code reuse** | `start()` written once, available to `Car`, `Bike`, `Truck` |
| **Polymorphism support** | A parent-type reference can point to any child object (next topic) |
| **Logical hierarchy** | Models real-world classification |

#### Terminology

- Parent = superclass = base class. Child = subclass = derived class.
- `extends` keyword. A class can extend **only one** class.
- **Every class implicitly extends `java.lang.Object`** (if it does not extend anything else), which is why every object has `toString()`, `equals()`, `hashCode()`.

#### What a reference type can call (important)

```java
class Student { void markAttendance() { System.out.println("Attendance marked"); } }
class EngineeringStudent extends Student { void attendLab() { System.out.println("Lab attended"); } }

EngineeringStudent es = new EngineeringStudent();
es.markAttendance();   // OK: inherited
es.attendLab();        // OK: own

Student s = new Student();
// s.attendLab();      // ERROR: Student has no attendLab()

Student s2 = new EngineeringStudent();   // allowed (upcasting)
// s2.attendLab();     // ERROR: the *reference type* decides what you can call
```

**Rule:** the **reference type** decides which members the compiler lets you call. The **actual object** decides which overridden implementation runs (polymorphism, next lecture).

#### Beyond the video: what is and is not inherited

| Member | Inherited? |
|---|---|
| `public` / `protected` fields and methods | Yes |
| Package-private members | Only if the child is in the **same package** |
| `private` members | **Not accessible** in the child. (The private *fields do exist* in the object's memory, since the parent part is built, but the child cannot reference them directly. Use protected/public accessors.) |
| **Constructors** | **Never inherited.** A child must define its own and call the parent's via `super(...)` |
| `static` members | Accessible via the child class name, but **hidden, not overridden** |
| `final` methods | Inherited, but **cannot be overridden** |

**Access modifier and inheritance:** if the parent method is `private`, the child cannot call it (video example: the compiler error "method not visible" appears in both the parent's outside callers and child).

#### Overriding (the video calls it "shadowing")

```java
class Vehicle { void start() { System.out.println("Vehicle starts"); } }
class Car extends Vehicle {
    @Override                                   // always use: compiler verifies you really override
    void start() { System.out.println("Car starts with a key"); }
}

Vehicle v = new Car();
v.start();      // "Car starts with a key" -> decided at RUNTIME by the actual object
```

Rules: same name and parameters, return type same or **covariant** (subtype), visibility **same or wider**, cannot throw broader **checked** exceptions, cannot override `final`, `static` or `private` methods.

#### Beyond the video: field hiding vs method overriding

```java
class A { int x = 4;  void show() { System.out.println("A.show"); } }
class B extends A {
    int x = 5;                                   // hides A.x (a different variable)
    @Override void show() { System.out.println("B.show " + x + " " + super.x + " " + this.x); }  // B.show 5 4 5
}

A ref = new B();
System.out.println(ref.x);   // 4  -> fields resolved by REFERENCE type (compile time)
ref.show();                  // B.show 5 4 5 -> methods resolved by OBJECT type (runtime)
```

Do not reuse field names in subclasses. It is a source of confusing bugs.

#### Beyond the video: when NOT to use inheritance

- **IS-A vs HAS-A:** use inheritance for true IS-A. Use **composition** (a field holding another object) for HAS-A. A `Car` *has an* `Engine`; it is not one.
- **Composition over inheritance** is a widely accepted guideline: inheritance creates **tight coupling** and the **fragile base class problem** (a change in the parent can silently break children).
- **Liskov Substitution Principle:** a child must be usable wherever the parent is expected without breaking behaviour. `Square extends Rectangle` is the classic violation.
- Prevent unwanted extension with `final class` (e.g. `String`); Java 17+ `sealed` classes allow a controlled list of subclasses.
- Design for inheritance or prohibit it (Effective Java, Item 19).



### 5. Types of inheritance

| Type | Shape | Supported in Java (classes)? |
|---|---|---|
| **Single** | `A → B` | Yes |
| **Multilevel** | `A → B → C` | Yes |
| **Hierarchical** | `A → B`, `A → C` (one parent, many children) | Yes |
| **Multiple** | `A, B → C` (many parents) | **No** (with classes) |
| **Hybrid** | mix of the above | Only if it avoids multiple class inheritance |

```java
// Single
class Student { }
class EngineeringStudent extends Student { }

// Multilevel: C inherits everything accessible from B and from A
class CseStudent extends EngineeringStudent { }

// Hierarchical: two children of the same parent
class MedicalStudent extends Student { }      // sibling of EngineeringStudent
```

- In multilevel, `CseStudent` can use every non-private member of `EngineeringStudent` **and** `Student`.
- Siblings (`EngineeringStudent`, `MedicalStudent`) do **not** share each other's members. Common members belong in the parent.

#### Multiple inheritance: not allowed for classes

```java
class C extends A, B { }     // COMPILE ERROR
```



### 6. The diamond problem (why multiple class inheritance is banned)

```
        A  (show())
       / \
      B   C     (both override show())
       \ /
        D  ← which show() does D inherit?
```

If `D` could extend both `B` and `C`, calling `d.show()` would be ambiguous. The same applies to **fields**: which copy of the parent's state does `D` get? Java avoids the whole class of problems (unlike C++, which needs `virtual` inheritance) by allowing only **one superclass**.

#### How Java still gets multiple inheritance: interfaces

A class may `implements` many interfaces (multiple inheritance of **type**). Since Java 8, interfaces can have `default` methods, so a diamond can appear, and Java forces you to resolve it:

```java
interface A { default void show() { System.out.println("A"); } }
interface B extends A { default void show() { System.out.println("B"); } }
interface C extends A { default void show() { System.out.println("C"); } }

class D implements B, C {
    @Override
    public void show() {
        B.super.show();       // explicit choice: call B's version (or C.super.show(), or your own logic)
    }
}
```

Without the override, `D` fails to compile ("inherits unrelated defaults"). **Conflict-resolution rules:** (1) a class's own or inherited **class** method always wins over an interface default; (2) the **more specific** interface wins; (3) otherwise you must override and choose explicitly.

Interfaces have **no instance state**, which is why multiple inheritance of *behaviour* is manageable in Java while multiple inheritance of *state* is not.



### 7. The `super` keyword

`this` refers to the **current object**. `super` refers to the **parent-class part** of the current object: a way to reach the parent's members that the child has hidden or overridden. It has **three uses**.

#### Use 1: access a parent's field (when hidden)

```java
class A { int x = 4; }
class B extends A {
    int x = 5;
    void print() {
        System.out.println(x);         // 5 (own)
        System.out.println(super.x);   // 4 (parent's)
    }
}
```

If there is no name clash, `super.` is optional.

#### Use 2: call a parent's method (when overridden)

```java
class Student {
    void print() { System.out.println("Name/Age/Roll"); }
}
class EngineeringStudent extends Student {
    @Override
    void print() {
        super.print();                          // reuse the parent's logic first
        System.out.println("College: IIT G");   // then extend it
    }
}
```

This is the standard pattern for **extending** rather than replacing behaviour.

#### Use 3: call a parent's constructor: `super(...)`

```java
class Student {
    String name; int age; int rollNumber;
    Student() { }                                          // explicit no-arg constructor
    Student(String name, int age, int rollNumber) {
        this.name = name; this.age = age; this.rollNumber = rollNumber;
    }
}

class EngineeringStudent extends Student {
    String college;
    EngineeringStudent(String name, int age, int rollNumber, String college) {
        super(name, age, rollNumber);      // parent initializes its own fields
        this.college = college;            // child initializes its own
    }
}

EngineeringStudent es = new EngineeringStudent("Aditya", 28, 1001, "IIT Guwahati");
```

**Rules for constructors and `super`:**

| Rule | Detail |
|---|---|
| Parent constructor always runs **first** | The parent part of the object must exist before the child part. |
| `super()` is **inserted implicitly** | If you don't write it, the compiler adds `super();` (no-arg) as the first line of every constructor. |
| Implicit `super()` needs a **no-arg parent constructor** | If the parent only has parameterized constructors (or the video's case: you defined one, so Java stops generating the default), the child fails to compile with *"constructor Student in class Student cannot be applied"*. Fix: call `super(args)` explicitly or add a no-arg constructor. |
| `super(...)` must be the **first statement** | Classic rule. (Newer Java, 25+, relaxes this to allow statements that don't touch `this` before `super(...)`.) |
| `super(...)` and `this(...)` cannot both appear | Both must be first. A constructor may chain via `this(...)`, and the chain must end in one that calls `super(...)`. |
| `super` is **unavailable in static context** | Static methods have no object. |
| Constructors are not inherited | Each class writes its own. |

#### Constructor chaining order

```java
class A {
    A()      { System.out.println("A()"); }
    A(int x) { System.out.println("A(int)"); }
}
class B extends A {
    B()      { System.out.println("B()"); }              // implicit super()
    B(int x) { super(x); System.out.println("B(int)"); }
}

new B();      // A()  B()
new B(5);     // A(int)  B(int)
```

Combine with the earlier init-order notes: **parent static → child static → parent instance init → parent constructor → child instance init → child constructor.**

#### Beyond the video

- `super.method()` is a **non-virtual, direct call** to the parent's implementation (`invokespecial` in bytecode), so it does not get overridden again by a grand-child.
- **Do not call overridable methods from a constructor.** The parent constructor runs before the child's fields are initialized, so an overridden method can see default values (`null`/`0`).
```java
class Parent { Parent() { init(); } void init() { } }
class Child extends Parent {
    String name = "x";
    @Override void init() { System.out.println(name); }   // prints null when called from Parent()
}
new Child();   // null
```
- You can only reach the **immediate** parent with `super`. To bypass a level, restructure the design (or use interface `X.super.method()` syntax for default methods).
- Inside an inner class, `Outer.this` accesses the outer instance (different from `super`).



### 8. Full example: everything together

```java
// file: com/example/college/Student.java
package com.example.college;

public class Student {
    private final String name;
    private int age;
    private final int rollNumber;

    public Student(String name, int age, int rollNumber) {
        this.name = name;
        setAge(age);                          // reuse validation
        this.rollNumber = rollNumber;
    }

    public String getName()  { return name; }
    public int getAge()      { return age; }
    public int getRollNumber() { return rollNumber; }

    public void setAge(int age) {
        if (age < 0 || age > 120) throw new IllegalArgumentException("Invalid age: " + age);
        this.age = age;
    }

    public void print() {
        System.out.println(name + ", " + age + ", " + rollNumber);
    }
}
```

```java
// file: com/example/college/EngineeringStudent.java
package com.example.college;

public class EngineeringStudent extends Student {
    private final String college;

    public EngineeringStudent(String name, int age, int rollNumber, String college) {
        super(name, age, rollNumber);          // parent constructor runs first
        this.college = college;
    }

    @Override
    public void print() {
        super.print();                          // parent's part
        System.out.println("College: " + college);
    }
}
```

```java
// file: com/example/Main.java
package com.example;

import com.example.college.EngineeringStudent;
import com.example.college.Student;

public class Main {
    public static void main(String[] args) {
        EngineeringStudent es = new EngineeringStudent("Aditya", 28, 1001, "IIT Guwahati");
        es.print();
        // Aditya, 28, 1001
        // College: IIT Guwahati

        Student s = es;                         // upcast: allowed
        s.print();                              // still prints both lines (runtime dispatch)
        // s.getCollege() would not compile: Student has no such method
    }
}
```



### 9. Quick comparison tables

| | Encapsulation | Inheritance |
|---|---|---|
| Purpose | Protect state and invariants | Reuse code and model IS-A |
| Mechanism | `private` + controlled methods | `extends` |
| Question it answers | *Who may touch this data?* | *What can I reuse from a more general type?* |

| | `this` | `super` |
|---|---|---|
| Refers to | Current object | Parent-class part of the current object |
| Field access | `this.x` | `super.x` |
| Method call | `this.m()` | `super.m()` |
| Constructor call | `this(...)` (same class) | `super(...)` (parent) |
| Usable as a value | Yes (`return this;`) | No |
| In static context | No | No |



### 10. Interview questions

| Question | Answer |
|---|---|
| What is encapsulation? | Bundling data and methods together and hiding the data behind a controlled public interface, so the object can enforce its own invariants. |
| Encapsulation vs abstraction? | Encapsulation hides *data/state*. Abstraction hides *complexity/implementation* and exposes only essential behaviour. |
| Are getters/setters always required? | No. Expose only what is needed. Prefer behaviour methods and immutability. Setters for every field defeat encapsulation. |
| How can a getter break encapsulation? | By returning a reference to a mutable internal object. Return a copy or unmodifiable view. |
| How many access modifiers? Order? | Four: `private` < package-private < `protected` < `public`. |
| What is the default access? | Package-private (no keyword). Interface members are `public`. |
| Can a top-level class be `private` or `protected`? | No. Only `public` or package-private. |
| Is `private` per object or per class? | Per **class**. Other instances of the same class can access it. |
| Who can access `protected`? | Same package, plus subclasses in other packages (through a reference of the subclass type). |
| Can a child reduce the visibility of an overridden method? | No. It may only keep or widen it. |
| Does `import` load the class or copy code? | No. It is a compile-time name shortcut. Classes load lazily at runtime. |
| Does `import java.util.*` include `java.util.concurrent`? | No. Sub-packages are separate. |
| Which package is imported by default? | `java.lang`. |
| Why doesn't Java support multiple inheritance of classes? | To avoid the diamond problem (ambiguous methods and duplicated state) and complexity. Interfaces provide multiple inheritance of type. |
| How does Java handle the diamond with default methods? | The class must override and pick explicitly, e.g. `B.super.show()`. |
| Is a constructor inherited? | No. |
| What does the compiler insert if you don't write `super()`? | An implicit `super();`. It fails to compile if the parent has no accessible no-arg constructor. |
| Can `super(...)` and `this(...)` be in the same constructor? | No. Each must be the first statement. |
| Can `super` be chained (`super.super.x`)? | No. Not valid Java. |
| Do private members get inherited? | They exist in the object but aren't accessible from the child. Use protected/public accessors. |
| Method overriding vs field hiding? | Methods dispatch on the runtime object. Fields are resolved by the reference type. |
| What does every class extend? | `java.lang.Object`. |
| Inheritance vs composition? | Inheritance = IS-A, tight coupling. Composition = HAS-A, more flexible. Prefer composition unless it's a true IS-A. |
| Why avoid calling overridable methods in constructors? | The parent constructor runs before the child's fields are initialized, so the override may see default values. |
| Can we prevent inheritance? | `final class`, private constructors, or `sealed` classes (Java 17+). |


### 11. Summary / mental model

```
ENCAPSULATION                          INHERITANCE
─────────────────────────              ───────────────────────────────
private data                           class Child extends Parent
+ public behaviour (with rules)         Child IS-A Parent
= object protects its own state         reuses code, enables polymorphism

Access:  private < (package) < protected < public

Java class inheritance:  single ✔   multilevel ✔   hierarchical ✔   multiple ✘
Multiple types via interfaces (diamond resolved with X.super.method())

super.field      → parent's field (if hidden)
super.method()   → parent's implementation (extend, don't replace)
super(args)      → parent constructor, must be first, runs first
```

**Remember:**
1. Hide state (`private`), expose behaviour, and let methods enforce the rules.
2. Never leak mutable internals through getters. Never use `double` for money.
3. Default to the strictest access level that works.
4. `import` is a naming shortcut. Packages are namespaces, and the directory must match.
5. The reference type decides what you can call. The object type decides which override runs.
6. Constructors chain parent-first. `super()` is implicit and needs an accessible no-arg parent constructor.
7. Prefer composition. Use inheritance only for a real IS-A relationship, and design for it or forbid it.






## 17. Abstraction and Polymorphism

> **Scope:** Abstraction (low-level vs high-level), abstract classes, interfaces, abstract class vs interface, abstraction vs encapsulation, polymorphism (compile-time and runtime), and what `static`, `private`, `final` and fields do under polymorphism.
> Sections marked **Beyond the video** add the depth expected at 2-3 years of experience.



### 0. Corrections to the video (read first)

| Video says | Accurate version |
|---|---|
| Interface methods can only be declared, never defined ("old Java") | True only up to Java 7. Since **Java 8** interfaces can have `default` and `static` methods; since **Java 9**, `private` methods. Modern interfaces are *not* "100% abstract". |
| Interface = "pure what", abstract class = "partial what + how" | Historically right. Today the real differences are **state, constructors, and single vs multiple inheritance** (see section 6). |
| "Encapsulation is about security, abstraction is about hiding" | Better: **encapsulation protects an object's invariants** (hides *data*, at implementation level). **Abstraction hides complexity and exposes an essential contract** (hides *implementation*, at design level). `private` is not a security boundary (reflection can bypass it). |
| Compile-time polymorphism = overloading, runtime = overriding | Correct as the standard interview answer. Strictly, overloading is "ad hoc" polymorphism (just different methods sharing a name), while overriding is **subtype** polymorphism, the one that gives OOP its power. |
| "Object is created at runtime, hence runtime polymorphism" | The real reason: the method that runs is chosen by the **actual object's class at runtime** (dynamic dispatch via `invokevirtual`), not by the reference type. |
| "Fields don't take part in polymorphism" | Correct. Fields are **hidden**, not overridden, and resolved by **reference type at compile time**. |
| "Interface names should end in -able; C# uses `I` prefix" | `-able` is a convention for *capability* interfaces (`Comparable`, `Runnable`). Many interfaces are plain nouns (`List`, `Map`, `Collection`). The `IFoo` prefix is **not** Java style. |
| "Reference of type Car can hold ElectricCar, so coupling is removed" | Coupling is **reduced**, not removed: callers depend only on the abstraction (`Car`), and only the code that calls `new ElectricCar()` knows the concrete type. Frameworks (Spring DI) remove even that. |
| "An abstract class must have abstract methods" | No. An abstract class **may have zero** abstract methods (it is just non-instantiable). But **any** abstract method forces the class to be abstract. |



### 1. What is abstraction?

> **Abstraction:** focus on **what** something does, ignore **how** it does it.

- Real world: you drive a car using steering, pedals, gear stick. You don't need to know how combustion works. An ATM offers `deposit`, `withdraw`, `checkBalance`; the internals are hidden.
- OOP models the **idea/perception** of an object, not the full reality. A `Teacher` class has `teach()` and `takeAttendance()`, not biology.

Two things abstraction demands:
1. **Model only what is necessary** (leave out irrelevant detail).
2. **Expose an interface for use, hide implementation details.**

#### Two levels in Java

| Level | Idea | Java mechanism |
|---|---|---|
| **Low-level** | Hide implementation details from users of a class | Plain **classes** + methods (users call `car.start()` without knowing the body) |
| **High-level** | **Separate *what* from *how*** so the caller isn't tied to one implementation | **Abstract classes** and **interfaces** |

#### Low-level abstraction (already familiar)

```java
class Car {
    void start()      { /* internals hidden */ }
    void accelerate() { /* internals hidden */ }
    void brake()      { /* internals hidden */ }
}

Car c = new Car();
c.start();          // caller knows WHAT, not HOW
```

#### The problem that high-level abstraction solves: tight coupling

```java
FuelCar     fc = new FuelCar();       // caller is tied to concrete classes
ElectricCar ec = new ElectricCar();   // every new type = new variable type, new code paths
fc.drive();
ec.drive();
```

`drive()` means the same thing (**what**) but is implemented differently (**how**). If callers hold concrete types, every new kind of car (diesel, hydrogen) forces changes in callers. The fix: let callers hold a **common supertype** and let the **runtime object** supply the *how*.

```java
Car c = new ElectricCar();   // or new FuelCar(), decided at runtime
c.drive();                   // right implementation runs
```

Abstract classes and interfaces formalize this.



### 2. Abstract classes

An **abstract class** is a class declared with `abstract` that **cannot be instantiated**. It may contain **abstract methods** (declaration only, no body) and **concrete methods** (with bodies).

```java
abstract class Car {

    void start() {                                     // concrete: shared behaviour
        System.out.println("Car started");
    }

    abstract void accelerate();                        // declared only: subclasses must define
    abstract void brake();
}

class FuelCar extends Car {
    @Override void accelerate() { System.out.println("Fuel car accelerating"); }
    @Override void brake()      { System.out.println("Fuel car stopping"); }
}

class ElectricCar extends Car {
    @Override void accelerate() { System.out.println("Electric car accelerating"); }
    @Override void brake()      { System.out.println("Regenerative braking"); }
}

public class Demo {
    public static void main(String[] args) {
        // Car c = new Car();        // ERROR: Car is abstract; cannot be instantiated
        Car c = new ElectricCar();   // or new FuelCar()
        c.start();                   // Car started            (inherited)
        c.accelerate();              // Electric car accelerating (runtime choice)
        c.brake();                   // Regenerative braking
    }
}
```

#### Rules

| Rule | Detail |
|---|---|
| Any abstract method ⇒ class must be `abstract` | The compiler enforces it. |
| Cannot `new` an abstract class | It could have undefined methods. |
| Concrete subclass **must implement all** inherited abstract methods | Otherwise the subclass itself must be declared `abstract` (the obligation passes down). |
| Abstract methods have **no body** (`;` instead of `{}`) | |
| `abstract` cannot combine with `final`, `private`, or `static` | Each contradicts the need to override. |
| Abstract class **may have** fields, constructors, concrete methods, static methods, nested types | Unlike interfaces, it can hold **instance state**. |
| Abstract class may have **zero** abstract methods | Used to prevent direct instantiation. |
| `@Override` is optional but **always use it** | Compiler catches typos and signature mismatches. |

#### Beyond the video

**1. Constructors in abstract classes.** You cannot call `new Car()`, but the constructor exists and **runs when a subclass is instantiated** (via `super(...)`), initializing shared state:

```java
abstract class Shape {
    private final String name;
    protected Shape(String name) { this.name = name; }
    String getName() { return name; }
    abstract double area();
}
class Circle extends Shape {
    private final double r;
    Circle(double r) { super("circle"); this.r = r; }
    @Override double area() { return Math.PI * r * r; }
}
```

**2. Anonymous subclass** lets you "instantiate" an abstract type on the fly (you are actually creating an unnamed subclass):
```java
Shape s = new Shape("unit") { @Override double area() { return 1; } };
```

**3. Template Method pattern**: the classic use of abstract classes. Fix the algorithm skeleton (`final`), leave steps abstract.
```java
abstract class ReportJob {
    public final void run() { load(); process(); export(); }   // final: order can't be changed
    protected abstract void load();
    protected abstract void process();
    protected void export() { System.out.println("default export"); }  // optional hook
}
```

**4. Pitfall: don't call abstract/overridable methods from a constructor.** The parent constructor runs before the child's fields are initialized (see the inheritance notes).



### 3. Interfaces

An **interface** defines a **contract**: a set of capabilities a class promises to provide. It says *what* the implementer can do, not *how*. It is **not a blueprint of an object** (you can't instantiate it).

```java
interface Car {
    void start();          // implicitly public abstract
    void accelerate();
    void brake();
}

class FuelCar implements Car {
    @Override public void start()      { System.out.println("Fuel car started"); }
    @Override public void accelerate() { System.out.println("Fuel car accelerating"); }
    @Override public void brake()      { System.out.println("Fuel car stopping"); }
}

Car c = new FuelCar();     // Car c = new Car(); is illegal
c.start();
```

- Class **`extends`** a class, **`implements`** an interface. An interface **`extends`** other interfaces (can extend several).
- Methods are **implicitly `public`**. So in the implementing class you **must** write `public`, otherwise: *"attempting to assign weaker access privileges"* (the video's error *"cannot reduce the visibility of the inherited method"*). Overriding can never reduce visibility.

#### What an interface can contain (modern Java)

| Member | Since | Notes |
|---|---|---|
| Abstract methods | 1.0 | Implicitly `public abstract` |
| **Constants** (fields) | 1.0 | Implicitly `public static final` |
| **`default` methods** | Java 8 | Have a body; inherited by implementers; can be overridden |
| **`static` methods** | Java 8 | Called as `InterfaceName.method()`; **not inherited** |
| **`private` methods** | Java 9 | Share code between default methods |
| Nested types | 1.0 | Implicitly `public static` |
| **Instance fields / constructors** | Never | Interfaces hold **no instance state** |

```java
interface Vehicle {
    int MAX_SPEED = 200;                           // public static final
    void start();

    default void honk() { System.out.println("Beep"); }   // optional to override

    static Vehicle noOp() { return () -> { }; }            // static factory (lambda works: single abstract method)
}
```

**Why default methods exist:** to add methods to widely used interfaces (e.g. `List.sort`, `Collection.stream`) **without breaking** every existing implementation.

#### Naming

`-able` names describe a capability: `Comparable`, `Runnable`, `Serializable`, `Flyable`. Noun names are equally common (`List`, `Comparator`). Do **not** prefix with `I` in Java.

```java
interface Flyable { void fly(); }
class Bird implements Flyable     { public void fly() { System.out.println("Flap wings"); } }
class Airplane implements Flyable { public void fly() { System.out.println("Use engines"); } }
```

`Bird` and `Airplane` share **no family**, only a **capability**. That is exactly what interfaces express.

#### Beyond the video

- **Multiple inheritance of type:** `class C implements A, B` is fine (diamond conflicts in default methods must be resolved explicitly, see the inheritance notes: `A.super.m()`).
- **Functional interface:** exactly one abstract method (`Runnable`, `Comparator`, `Function`). Can be implemented with a **lambda**; annotate with `@FunctionalInterface`.
- **Marker interface:** no methods (`Serializable`, `Cloneable`), it tags a class.
- **Sealed interfaces (Java 17):** `sealed interface Shape permits Circle, Square {}` restricts implementers.
- **Program to an interface:** `List<String> list = new ArrayList<>();`. Swap `ArrayList` for `LinkedList` without touching callers. Spring injects implementations into fields typed by interface.
- **Interface Segregation:** prefer several small interfaces over one large one.



### 4. Low-level vs high-level: putting it together

```
              ┌────────────────┐
  caller ───► │  Car (abstract │  ← the WHAT (contract)
              │  class/iface)  │
              └───────┬────────┘
             ┌────────┴────────┐
        ┌────▼────┐       ┌────▼────────┐
        │FuelCar  │       │ElectricCar  │   ← the HOW (implementations)
        └─────────┘       └─────────────┘
```

Callers depend on `Car`. Implementations can be added, swapped, or mocked in tests without changing callers (**Open/Closed Principle**, **Dependency Inversion**).



### 5. Abstract class vs interface

| | Abstract class | Interface |
|---|---|---|
| Purpose | Family of **closely related** classes; share code and state ("**is-a** kind of") | A **capability/role/contract** any class can adopt ("**can-do**") |
| Methods | Abstract + concrete | Abstract + `default` + `static` + `private` |
| Fields | Any (instance, static, any access) | Only constants (`public static final`) |
| Constructors | Yes | No |
| Instance state | Yes | No |
| Access modifiers on methods | Any | Public (private since Java 9 for helpers) |
| Inheritance | A class can extend **only one** | A class can implement **many** |
| Relation to hierarchy | `extends` | `implements` / `extends` (interface to interface) |
| Instantiation | No | No |
| Typical use | Template Method, shared base logic | Strategy, plugin APIs, DI, callbacks |

**Choosing:**
- Need **shared state or code** among related classes? → abstract class.
- Need **unrelated classes** to share a capability, or need **multiple inheritance of type**? → interface.
- Default to an interface for APIs; add an abstract skeletal class if there is common logic (e.g. `List` + `AbstractList`).
- It is often both: an interface for the contract plus an abstract class as a convenience base.


### 6. Abstraction vs encapsulation

| | Abstraction | Encapsulation |
|---|---|---|
| Focus | **What** the object exposes; hides complexity | **How the data is protected**; bundles data + methods |
| Level | Design level | Implementation level |
| Achieved by | Abstract classes, interfaces | Access modifiers (`private`), getters/setters, immutability |
| Hides | Implementation details (behaviour) | Internal state (data) |
| Example | `Car` interface: drive without knowing engine | `BankAccount.balance` is `private`; change via `deposit()` |
| Analogy | Car pedals | The bonnet is closed (and locked to prevent tampering) |

They complement each other: abstraction defines the public contract; encapsulation guards the internals that fulfil it. An interview answer: *"Abstraction is about **what** you expose, encapsulation is about **how** you protect what's inside."*


### 7. Polymorphism

**Poly** (many) + **morph** (forms): the same operation behaves differently depending on context.

Real-world: a human told to "run" runs at one speed normally, and faster if a dog chases (same object, same command, different parameters). A dog, a duck, and a human told to "run" each run differently (different objects, same command).

| Type | Also called | Mechanism | Resolved |
|---|---|---|---|
| **Compile-time** | Static / early binding | Method **overloading** | By the compiler, from **argument types** |
| **Runtime** | Dynamic / late binding | Method **overriding** | By the JVM, from the **actual object's class** |



### 8. Compile-time polymorphism: method overloading

Same method name, **different parameter list** (number, type, or order).

```java
class Human {
    void run()                       { System.out.println("Running at 2 km/h"); }
    void run(boolean dogBehind)      { System.out.println(dogBehind ? "Running at 5 km/h" : "Running at 2 km/h"); }
    void run(int km, String terrain) { System.out.println("Running " + km + "km on " + terrain); }
}

Human h = new Human();
h.run();            // picked at compile time from the call's signature
h.run(true);
h.run(3, "trail");
```

**Rules**
- Parameter lists must differ. **Return type alone cannot distinguish** overloads (compile error).
- Access modifiers and thrown exceptions can differ freely.
- Works within one class or across parent and child.

#### Beyond the video: overload resolution

The compiler picks the overload by **compile-time (static) types** of the arguments, in three phases: (1) exact/widening primitive conversion, (2) boxing/unboxing, (3) varargs. The **most specific** applicable method wins.

```java
static void f(Object o) { System.out.println("Object"); }
static void f(String s) { System.out.println("String"); }

f("hi");                 // String
Object o = "hi";
f(o);                    // Object  <- static type is Object, though the object is a String
f(null);                 // String  (most specific applicable)

static void g(long x)      { System.out.println("long"); }
static void g(Integer x)   { System.out.println("Integer"); }
static void g(int... x)    { System.out.println("varargs"); }
g(5);                    // long (widening beats boxing beats varargs)
```

Pitfalls: ambiguous calls (`f(null)` with two unrelated reference types) are compile errors; overloading with varargs and boxing can surprise readers. Overloading is **not dynamic**: it never looks at the runtime object.


### 9. Runtime polymorphism: method overriding

A subclass provides its own implementation of an inherited method. Which version runs depends on the **actual object**.

```java
abstract class Animal { abstract void run(); }

class Dog   extends Animal { @Override void run() { System.out.println("Dog runs on four legs"); } }
class Duck  extends Animal { @Override void run() { System.out.println("Duck waddles"); } }
class Human extends Animal { @Override void run() { System.out.println("Human jogs"); } }

Animal a = new Dog();
a.run();          // Dog runs on four legs
a = new Human();
a.run();          // Human jogs           <- same reference, same call, different behaviour
```

Why "runtime"? Which subclass is created may only be known while the program runs (user input, config, DB):

```java
static Animal create(String type) {
    return switch (type) {
        case "dog"   -> new Dog();
        case "duck"  -> new Duck();
        case "human" -> new Human();
        default      -> throw new IllegalArgumentException("Unknown type: " + type);
    };
}

for (Animal a : List.of(create("dog"), create("duck"), create("human"))) {
    a.run();      // caller code never changes when a new Animal type is added
}
```

#### Rules for overriding

| Rule | Detail |
|---|---|
| Same **name and parameter types** | Otherwise it's an overload, not an override. `@Override` catches this. |
| **Return type** | Same, or a **subtype** (covariant return) |
| **Access** | Same or **wider** (never narrower) |
| **Checked exceptions** | Same or narrower; cannot add broader checked exceptions (unchecked are fine) |
| Cannot override | `static` (hidden), `private` (not inherited), `final` methods; constructors |
| Reference type decides **what you may call**; object type decides **which implementation runs** | |

```java
class Animal { Animal reproduce() { return new Animal(); } }
class Dog extends Animal { @Override Dog reproduce() { return new Dog(); } }   // covariant return
```

#### Beyond the video: how the JVM does it

- Each class has a **virtual method table (vtable)**. `invokevirtual` looks up the method through the object's **actual class** at runtime.
- Calls to `private`, `static`, `final` methods and constructors use direct calls (`invokespecial`/`invokestatic`); no dynamic lookup.
- The JIT can **inline** monomorphic call sites (only one implementation ever seen), so dispatch is usually cheap.

**Upcasting and downcasting**

```java
Animal a = new Dog();          // upcast: implicit, always safe
// a.bark();                   // ERROR: Animal has no bark()

if (a instanceof Dog d) {      // pattern matching (Java 16+): check + cast in one step
    d.bark();
}
Dog d2 = (Dog) a;              // explicit downcast: ClassCastException if a is not a Dog
```

Frequent downcasting or long `instanceof` chains are a design smell. Add the method to the abstraction instead.



### 10. What does *not* take part in overriding

| Member | Behaviour under polymorphism | Why |
|---|---|---|
| **`static` method** | **Hidden**, resolved by **reference type** | Belongs to the class, not to an object |
| **`private` method** | **Not overridden**; a same-named child method is unrelated | Not visible to the child |
| **`final` method** | **Cannot be overridden** (compile error) | Intentionally locked |
| **Fields (instance vars)** | **Hidden**, resolved by **reference type** | Only methods dispatch dynamically |
| **Constructors** | Never inherited or overridden | |

```java
class A {
    static void hello() { System.out.println("A.hello"); }
    int x = 10;
    int getX() { return x; }
    private void secret() { System.out.println("A.secret"); }
    void callSecret()     { secret(); }
}
class B extends A {
    static void hello() { System.out.println("B.hello"); }     // hides
    int x = 20;                                                // hides
    @Override int getX() { return x; }                         // overrides
    void secret() { System.out.println("B.secret"); }          // new, unrelated method
}

A a = new B();
a.hello();         // A.hello   (static: reference type)
System.out.println(a.x);        // 10   (field: reference type)
System.out.println(a.getX());   // 20   (method: actual object)
a.callSecret();    // A.secret  (A's callSecret calls A's private secret)
```

**Rule of thumb:** *only instance methods are polymorphic.* To get field-like polymorphism, expose the value through a method (`getX()`), which also fits encapsulation.

#### `final` and inheritance recap

```java
class P { final void lock() { } }
class C extends P { /* void lock() { }  ERROR: cannot override final method */ }

final class Sealed { }
// class Sub extends Sealed { }        // ERROR: cannot inherit from final class
```

Use `final class` for immutable or security-sensitive types (`String`). Use `final` methods to protect an algorithm (Template Method).


### 11. Overloading vs overriding

| | Overloading | Overriding |
|---|---|---|
| Polymorphism type | Compile-time (static) | Runtime (dynamic) |
| Where | Same class or across hierarchy | Subclass vs superclass |
| Signature | **Must differ** in parameters | **Must match** |
| Return type | Can differ (but not alone) | Same or covariant |
| Access | Any | Same or wider |
| Decided by | Reference/argument **static types** | Actual **object** |
| `static` / `private` / `final` | Can be overloaded | Cannot be overridden |
| Needs inheritance | No | Yes |



### 12. Full example: abstraction + polymorphism

```java
interface Notifier {                         // the WHAT
    void send(String to, String message);
}

class EmailNotifier implements Notifier {    // HOW #1
    @Override public void send(String to, String msg) { System.out.println("Email to " + to + ": " + msg); }
}
class SmsNotifier implements Notifier {      // HOW #2
    @Override public void send(String to, String msg) { System.out.println("SMS to " + to + ": " + msg); }
}

class OrderService {
    private final Notifier notifier;                     // depends on the abstraction
    OrderService(Notifier notifier) { this.notifier = notifier; }   // injected
    void placeOrder(String user) {
        // ... business logic ...
        notifier.send(user, "Order placed");             // polymorphic call
    }
}

public class Main {
    public static void main(String[] args) {
        new OrderService(new EmailNotifier()).placeOrder("a@x.com");   // Email to a@x.com: Order placed
        new OrderService(new SmsNotifier()).placeOrder("999");         // SMS to 999: Order placed
        // Adding PushNotifier requires ZERO changes in OrderService.
    }
}
```

In tests you can pass a fake `Notifier` (mock). This is the real payoff of abstraction plus polymorphism.



### 13. Interview questions

| Question | Answer |
|---|---|
| What is abstraction? | Exposing *what* an object does while hiding *how*; achieved with abstract classes and interfaces (and plain classes at low level). |
| Abstraction vs encapsulation? | Abstraction hides complexity and defines a contract (design). Encapsulation hides data and protects invariants (implementation). |
| Can we instantiate an abstract class? | No. But its constructor runs when a subclass is created, and an anonymous subclass can be instantiated. |
| Can an abstract class have a constructor? | Yes. It initializes shared state and runs via `super(...)`. |
| Can an abstract class have no abstract methods? | Yes. It just prevents direct instantiation. |
| Can `abstract` combine with `final`/`static`/`private`? | No: each conflicts with overriding. |
| What if a subclass doesn't implement all abstract methods? | It must be declared `abstract` too, otherwise compile error. |
| Can an interface have method bodies? | Yes: `default`, `static` (Java 8) and `private` (Java 9) methods. |
| Can an interface have fields? | Only constants (`public static final`). No instance state. |
| Why must implementing methods be `public`? | Interface methods are implicitly public, and overriding can't reduce visibility. |
| Can a class implement multiple interfaces? | Yes; that's how Java gets multiple inheritance of type. Only one superclass. |
| Abstract class vs interface? | See the table: shared state/code among related classes vs a capability contract; single vs multiple inheritance. |
| What is a functional interface? | An interface with exactly one abstract method; usable with lambdas. |
| What is a marker interface? | An interface with no methods used to tag a class (`Serializable`). |
| Why did default methods get introduced? | Evolve interfaces (e.g. `Collection.stream`) without breaking existing implementers. |
| What is polymorphism? | One interface, many implementations; the same call behaves differently based on context. |
| Types of polymorphism in Java? | Compile-time (overloading) and runtime (overriding). |
| Can we overload `main`? | Yes, but only `public static void main(String[])` is the entry point. |
| Can we override a static method? | No. It is hidden, and the reference type decides which runs. |
| Can we override a private method? | No. It isn't inherited; a child method with the same name is a new method. |
| Can we overload by return type only? | No. Compile error. |
| Are fields polymorphic? | No. Fields are hidden and resolved by the reference type. |
| What decides which method runs at runtime? | The **actual object's class** (dynamic dispatch); the **reference type** only decides what the compiler allows. |
| What is upcasting and downcasting? | Upcast: child to parent (implicit, safe). Downcast: parent to child (explicit, may throw `ClassCastException`); guard with `instanceof`. |
| What is covariant return type? | An overriding method may return a subtype of the parent's return type. |
| Can an overriding method throw a broader checked exception? | No. Same or narrower (unchecked are unrestricted). |
| How does the JVM implement dynamic dispatch? | vtable lookup via `invokevirtual` on the object's actual class. |
| What is "programming to an interface"? | Declare variables, parameters, and returns using the abstract type (`List`), not the concrete one (`ArrayList`). |



### 14. Summary / mental model

```
ABSTRACTION                               POLYMORPHISM
──────────────────────────────            ───────────────────────────────────
WHAT vs HOW                               one call, many behaviours
abstract class → family + shared code     compile-time: overloading (by argument types)
interface      → capability contract      runtime:      overriding  (by actual object)

Car c = new ElectricCar();                only INSTANCE METHODS dispatch dynamically
c.drive();   // ElectricCar.drive()       static / private / final / fields: NOT polymorphic
```

**Remember:**
1. Abstraction = expose the contract, hide the implementation. Encapsulation = protect the data.
2. Abstract class: can't instantiate, may hold state and constructors, single inheritance. Interface: contract, no state, multiple implementation.
3. Any abstract method forces `abstract` on the class; concrete subclasses must implement every abstract method.
4. Interface methods are `public`; implementations must be `public`. Modern interfaces have `default`/`static`/`private` methods.
5. Overloading = same name, different parameters, resolved at compile time by static types.
6. Overriding = same signature in a subclass, resolved at runtime by the actual object. Use `@Override`.
7. `static`, `private`, `final` methods and fields don't take part in overriding.
8. The reference type limits **what you can call**; the object type decides **what runs**.
9. Depend on abstractions, so adding a new implementation needs no changes to callers.


## 18. Class Internals: File Rules, Wrapper Classes, Boxing, Equality, Abstract Classes, POJOs



### 0. Corrections to the video (read first)

| Video says | Accurate version |
|---|---|
| "One public class per file, name must match" | Both are **Java Language Specification rules**, not JVM conveniences the video frames them as — but the video's *reasoning* (why the rule helps the JVM/compiler locate the entry point) is a correct and commonly cited justification. Precisely: a `.java` file may have **zero or one** public top-level type (class, interface, enum, record); if one exists, the file name must match it. A file can have **zero** public classes (all package-private) — the video implies you always need one, which isn't quite right. |
| `new Integer(10)` conceptual walkthrough | Correct for intuition, but the `Integer(int)` **constructor is deprecated since Java 9** and **removed in Java 21** — it will not compile on modern Java. Always use `Integer.valueOf(...)` or, better, rely on autoboxing. |
| Integer cache range "-128 to +127 (byte range)" | Correct default range, but it's **configurable**: `-XX:AutoBoxCacheMax=<n>` raises the upper bound (JVM-specific, HotSpot). Also applies to `Byte`, `Short`, `Long`, `Character` (0-127), and `Boolean` (`TRUE`/`FALSE`) — not just `Integer`. |
| "`equals()` comes from some parent, we'll see later" | It comes from **`java.lang.Object`**, which every class inherits (directly or indirectly). `Integer` overrides it to compare `int` values instead of references. |
| Abstract class Q&A: "final method can't be abstract... contradictory" | Precisely: `abstract` conflicts with `final`, `private`, and `static` because all three either prevent overriding (`final`, `private`) or don't participate in it (`static`) — but abstract *by definition* requires overriding. |
| POJO defined loosely | POJO strictly means: no framework-imposed constraints (no required base class, no required annotations, no required interface) — from Rod Johnson/Martin Fowler's original 2000 coinage, as a reaction against heavyweight EJB requirements. Whether it holds business logic (anemic vs rich) is a separate, later distinction, correctly separated in the video. |



### 1. Why only one public class per `.java` file, and why its name must match the file

**The rule:** A source file may contain **at most one `public` top-level type** (class, interface, enum, record). If it has one, the **file name must exactly match that type's name** (case-sensitive), e.g. `Demo.java` → `public class Demo`.

```java
// Demo.java
public class Demo { }          // OK: matches file name
class Helper { }                // OK: package-private, any number allowed
// public class Sample { }      // ERROR: "the public type Sample must be defined in its own file"
```

#### Why this rule exists

1. **The JVM needs an unambiguous entry point.** Execution always starts from a `main` method. If multiple public classes could exist per file, the JVM/launcher would have no deterministic way to know which class (if any) holds the `main` it should call.
2. **`main` is `public static`** so the JVM can call it without creating an instance (`static`) and from outside the package (`public`) — the file's public class is the one guaranteed accessible from anywhere, including the launcher.
3. **Fast class loading by file name.** When you run `java Demo`, the JVM looks for `Demo.class`, generated from `Demo.java`. If the class name didn't match the file name, the JVM (or `javac`) would need to open and scan every file to find the right class — expensive and ambiguous. Matching names make **lookup O(1)**: file name → class name → `.class` file, directly.
4. **Multi-file applications work the same way.** With many `.java` files, exactly one file's public class contains `main` (or is designated as the entry point in `pom.xml`/`build.gradle`/manifest). You compile everything, then run specifically **that** class: `java com.example.Demo`. The rule just guarantees that whichever file that is, `javac`/`java` can locate it unambiguously.

#### Beyond the video

- The rule is enforced by `javac`, not the JVM itself — it is a **language/compiler rule** (JLS §7.6), independent of how class loading later works.
- A compiled `.class` file's internal name (in its bytecode constant pool) is what the JVM actually resolves; the source-to-file mapping matters only at compile time.
- With build tools (Maven/Gradle) and IDEs, you rarely invoke `javac`/`java` by hand, but the underlying rule is unchanged and still enforced by the compiler.
- The **manifest's `Main-Class`** entry in an executable JAR is the modern equivalent of "which class has `main`" — it removes the need to type the class name at all: `java -jar app.jar`.



### 2. Wrapper classes

Java's type system splits into **primitives** (`int`, `long`, `short`, `byte`, `float`, `double`, `char`, `boolean`) and **non-primitives / reference types** (objects, arrays, user-defined types). For every primitive, Java provides a corresponding **wrapper class**:

| Primitive | Wrapper class |
|---|---|
| `int` | `Integer` |
| `long` | `Long` |
| `short` | `Short` |
| `byte` | `Byte` |
| `float` | `Float` |
| `double` | `Double` |
| `char` | `Character` |
| `boolean` | `Boolean` |

```java
int x = 10;                        // primitive: stored directly, e.g. on the stack (as a local var)
Integer y = new Integer(10);       // deprecated/removed constructor — conceptual illustration only
```

Conceptually, a wrapper class holds:
- a **private, final** primitive `value` field,
- constructors (mostly deprecated now),
- a static factory `valueOf(...)`,
- an instance accessor (`intValue()`, `doubleValue()`, etc.),
- an overridden `equals()`, `hashCode()`, `toString()`, `compareTo()`.

#### Why wrapper classes exist

1. **Java's collections and generics only work with objects**, never primitives. `ArrayList<int>` is illegal; `ArrayList<Integer>` works because `Integer` is a class.
2. **Object features** — nullability, methods, participation in the type hierarchy (`Object` superclass), utility methods (`Integer.parseInt`, `Integer.MAX_VALUE`, `Integer.compare`) — none of these exist on bare primitives.

#### Why primitives still exist (despite wrappers covering everything)

1. **Legacy**: Java's early design deliberately kept C/C++-style primitives to ease adoption by developers from those languages.
2. **Performance**: primitives are stored directly (no object header, no heap allocation, no indirection), making arithmetic and storage significantly faster and more memory-efficient than boxed equivalents. Primitives are used constantly, including inside collection-adjacent optimized paths (and are essential wherever performance matters, e.g. tight loops, large arrays).

#### Beyond the video

- **`Number`** is the common abstract superclass of `Integer`, `Long`, `Double`, etc. (not `Object` directly for numeric conversions) — it defines `intValue()`, `longValue()`, `doubleValue()`, etc.
- All wrapper classes are **immutable and `final`** (cannot be subclassed) — this guarantees their value never changes after creation, which is essential for safe caching and use as `HashMap` keys.
- **`Character`** and **`Boolean`** don't extend `Number` (no numeric conversion methods) since they aren't numeric types.
- Primitive arrays (`int[]`) and boxed arrays (`Integer[]`) are **not interchangeable**: `int[]` is more memory-efficient (no per-element object overhead); this matters heavily in performance-sensitive or large-data code.



### 3. Autoboxing and unboxing

| Term | Direction | Example |
|---|---|---|
| **Autoboxing** | primitive → wrapper | `Integer y = x;` (where `x` is `int`) |
| **Unboxing** | wrapper → primitive | `int y = x;` (where `x` is `Integer`) |

Both happen **automatically**, inserted by the **compiler** — no manual casting needed.

```java
int x = 10;
Integer y = x;              // autoboxing
// compiler internally rewrites to:  Integer y = Integer.valueOf(x);

Integer a = 20;
int b = a;                  // unboxing
// compiler internally rewrites to:  int b = a.intValue();
```

#### How autoboxing evolved

```java
Integer y = new Integer(x);          // old Java: direct constructor call (now deprecated/removed)
Integer y = Integer.valueOf(x);      // modern Java: factory method, uses internal caching (see §5)
```

Always prefer relying on the compiler's autoboxing, or call `Integer.valueOf(...)` explicitly if you must — never call the constructor.

#### Where boxing/unboxing kicks in

| Context | Example |
|---|---|
| **Assignment statements** | `Integer y = x;` / `int y = x;` |
| **Method calls (arguments and parameters)** | Passing an `int` where an `Integer` parameter is expected, or vice versa |
| **Arithmetic operations** | `Integer a = 10; Integer b = 20; int sum = a + b;` — both operands are unboxed, added as primitives, then the `int` result is stored (or reboxed if assigned to an `Integer`) |

```java
static void printInt(int x) { System.out.println(x); }

printInt(50);                // fine, no boxing needed
Integer boxed = 50;
printInt(boxed);             // unboxing: boxed.intValue() passed in

Integer a = 10, b = 20;
int sum = a + b;             // both unboxed, then added
```

#### Beyond the video

- Autoboxing/unboxing also happens in **enhanced for-loops**, **ternary expressions** (`condition ? intVal : IntegerVal`), and **collection operations** (`list.add(5)` on a `List<Integer>` autoboxes `5`).
- **Performance cost**: boxing/unboxing in tight loops (e.g. summing a `List<Integer>` in a loop) creates unnecessary object churn — a classic performance pitfall. Prefer primitive-based loops or streams with `IntStream`/`mapToInt` for hot paths.
- **Overload ambiguity**: if both `f(int)` and `f(Integer)` exist, the compiler prefers the **exact match** (`f(int)` for a primitive argument) over the boxed version, to minimize implicit conversions.



### 4. NullPointerException from unboxing

```java
Integer x = null;             // valid: Integer is an object, can hold null
int y = x;                    // compiles fine...
                               // ...but throws NullPointerException at RUNTIME
```

Because unboxing internally calls `x.intValue()`, and calling any method on a `null` reference throws `NullPointerException`. Primitives themselves can never be `null` — there is no "null int".

**Practical implication:** whenever mixing wrapper types and primitives (assignments, arithmetic, method calls), a `null` wrapper is a hidden NPE risk that the compiler will not catch. Common real-world trigger: a `Map<String, Integer>` lookup returning `null` for a missing key, then unboxing it directly:

```java
Map<String, Integer> scores = new HashMap<>();
int score = scores.get("missing");   // NPE: get() returns null, unboxing fails
int score = scores.getOrDefault("missing", 0);   // safe alternative
```



### 5. `==` vs `.equals()` and the Integer cache

**The core rule:** `==` on reference types (including wrapper classes) compares **references** (memory addresses), never the boxed value.

```java
int x = 10, y = 10;
System.out.println(x == y);              // true — primitives store values directly, so == compares values

Integer a = 200, b = 200;
System.out.println(a == b);              // false — two different Integer objects, different references
System.out.println(a.equals(b));         // true — compares actual int values
System.out.println(a.intValue() == b.intValue());   // true — compares unboxed values
```

- For **primitives**, `==` compares values directly (there's no object, no reference — the variable *is* the value).
- For **objects** (including wrapper classes), `==` asks *"do these two references point to the same object?"* — not *"do they hold equal data?"*.
- `.equals()` is an **instance method** that (for `Integer` and other wrappers) is overridden to compare the underlying values, not references.

#### Why small values sometimes print `true` with `==` anyway: the Integer cache

```java
Integer a = 100, b = 100;
System.out.println(a == b);              // true! — due to caching, NOT because == compares values

Integer c = 200, d = 200;
System.out.println(c == d);              // false — outside the cached range
```

**Why:** `Integer.valueOf(int)` maintains an internal cache of pre-created `Integer` objects for values **-128 to 127** (the `byte` range). When you autobox a value in this range, `valueOf` returns the **same cached object** instead of creating a new one — so two variables holding the same small value end up pointing to the **same reference**, making `==` appear to work "correctly" purely by coincidence.

```java
// Conceptual internals of Integer.valueOf
public static Integer valueOf(int i) {
    if (i >= -128 && i <= 127) {
        return IntegerCache.cache[i + 128];      // return pre-existing cached object
    }
    return new Integer(i);                        // create a new object outside the cache range
}
```

**Consequence:** relying on `==` for wrapper comparison is a **classic Java bug** — it silently works for small numbers (cache hits) and silently breaks for larger ones. **Always use `.equals()`** to compare wrapper objects, never `==`.

#### Beyond the video

- The cache also applies to `Byte`, `Short`, `Long` (-128 to 127), `Character` (0 to 127), and `Boolean` (`TRUE`/`FALSE` singletons) — not just `Integer`.
- `Integer.valueOf(int)` is guaranteed by the JLS to return cached instances for values in `[-128, 127]`; this is **specified behaviour**, not an implementation detail you can't rely on — though the *upper* bound beyond 127 is JVM-tunable and not guaranteed portable.
- For **user-defined classes**, always override both `equals()` and `hashCode()` together (contract: equal objects must have equal hash codes) — never override one without the other, since collections like `HashMap`/`HashSet` depend on both.
- `Objects.equals(a, b)` (from `java.util.Objects`) is a **null-safe** alternative to `a.equals(b)`, useful when either side might be `null`.



### 6. Abstract classes: interview-focused Q&A

Applying first-principles reasoning (what does each keyword actually mean?) resolves nearly every abstract-class question.

#### Q1: Can abstract classes have constructors?

**Yes.** Although you cannot `new` an abstract class directly, its constructor **does run** — via `super(...)` — whenever a concrete subclass is instantiated.

```java
abstract class Animal {
    private final String name;
    Animal(String name) { this.name = name; }        // runs via subclass instantiation
    abstract void makeSound();
}
class Dog extends Animal {
    Dog(String name) { super(name); }                 // explicitly calls parent constructor
    @Override void makeSound() { System.out.println("Barking"); }
}

Animal a = new Dog("Bruno");    // Animal's constructor runs, setting name = "Bruno"
```

#### Q2: Can an abstract class be `final`?

**No.** `final` on a class means "cannot be subclassed." `abstract` means "must be subclassed to be usable" (it has undefined behaviour that only a subclass can supply). The two are **directly contradictory** — the compiler rejects `final abstract class` outright: *"illegal combination of modifiers"*.

#### Q3: Can abstract classes have `static` methods/variables?

**Yes.** `static` members belong to the **class itself**, not to any instance. Since the class exists (even though you can't instantiate it), its static members work normally — accessed as `ClassName.staticMember`, no object required.

```java
abstract class Animal {
    static String kingdom = "Animalia";
    static void printKingdom() { System.out.println(kingdom); }
}
Animal.printKingdom();     // fine — no instantiation needed
```

#### Q4: Can abstract classes have `private` methods?

**Yes — but only if the private method is concrete (non-abstract).** A `private` method is inherently invisible outside the class, so it **cannot be `abstract`**: an abstract method must be visible to subclasses so they can override it, but subclasses can never see a `private` member to override in the first place. Combining `private abstract` is a compile error: *"abstract methods cannot have a private modifier"*.

```java
abstract class Animal {
    private void internalHelper() { }        // OK: concrete, hidden, used only inside this class
    // private abstract void eat();           // ERROR
    abstract void makeSound();                // OK: visible to subclasses
}
```

#### Q5: Can abstract classes have `final` methods?

**Yes — but only if the final method is concrete (non-abstract).** `final` means "cannot be overridden"; `abstract` means "must be overridden by a subclass." Combining them is contradictory — same reasoning as `private abstract`: *"illegal combination of modifiers: abstract and final"*.

```java
abstract class Animal {
    final void sleep() { System.out.println("Sleeping"); }   // OK: no subclass can override
    abstract void makeSound();                                 // OK
}
```

#### Q6: Can an abstract class have zero abstract methods?

**Yes.** `abstract` on a class only means "cannot be instantiated directly" — it does not require any abstract methods. A common reason: you want to force users through a subclass (perhaps for a builder/factory pattern or to prevent accidental direct use), while still providing full, ready-to-use default implementations for every method.

```java
abstract class Animal {                  // no abstract methods at all
    void makeSound() { System.out.println("Making sound"); }   // fully implemented
    void sleep()     { System.out.println("Sleeping"); }
}
// new Animal();   // still illegal — the class itself is abstract, regardless of method content
```

#### Summary table

| Question | Answer | One-line reason |
|---|---|---|
| Constructor? | Yes | Runs via `super()` when a subclass is instantiated |
| `final` class? | No | `final` (no subclassing) contradicts `abstract` (must subclass) |
| `static` members? | Yes | Belong to the class, not an instance — no instantiation needed |
| `private` method? | Yes, if concrete | A `private abstract` method would be invisible to the subclass that must implement it |
| `final` method? | Yes, if concrete | A `final abstract` method can't be overridden yet must be — contradiction |
| Zero abstract methods? | Yes | `abstract` only blocks direct instantiation; it doesn't require unfinished methods |



### 7. POJO classes

**POJO = Plain Old Java Object.** A simple class with **no framework-imposed constraints**: it doesn't have to extend a specific base class, implement a specific interface, or carry specific annotations to "qualify" — the term was coined (Rod Johnson / Martin Fowler, 2000) specifically as a reaction against heavyweight frameworks (like early EJB) that forced classes into rigid, framework-controlled shapes.

```java
public class Sample {
    private int x;
    private String y;

    public Sample(int x, String y) { this.x = x; this.y = y; }

    public int getX()       { return x; }
    public void setX(int x) { this.x = x; }
    public String getY()       { return y; }
    public void setY(String y) { this.y = y; }
}
```

Typical contents: **private fields**, a **constructor**, and **getters/setters**. May also include a builder (Builder pattern) or light business logic — the exact boundary is **subjective**, not a strict spec.

#### Why POJOs exist: modelling real-world entities

```java
public class Student {
    private String name;
    private int age;
    private int rollNumber;
    private String college;
    // constructor, getters, setters...
}
```

A POJO models a **domain entity** cleanly — e.g. mapping to a database table's columns (`name`, `age`, `rollNumber`, `college`), or serving as a DTO for API requests/responses. This is why POJOs are also called **model classes**. In frameworks like Spring/Hibernate, POJOs (often annotated, but the *class itself* stays framework-independent in structure) map directly to database rows or JSON payloads.

#### Anemic model vs rich domain model

| | Anemic model | Rich domain model |
|---|---|---|
| Contents | Fields + constructor + getters/setters only | Fields + constructor + getters/setters **+ business logic** |
| Example | `Student` with just `name`, `getName()`, `setName()` | `Student` also has `markAttendance()` |
| Both are POJOs? | Yes | Yes |
| When to use | Pure data transfer / simple modelling | When behaviour belongs intrinsically to the entity |

```java
// Anemic model
public class Student {
    private String name;
    public Student(String name) { this.name = name; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

// Rich domain model
public class Student {
    private String name;
    private boolean present;
    public Student(String name) { this.name = name; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public void markAttendance() { this.present = true; }   // business logic lives with the data
}
```

Both are still POJOs — the anemic/rich distinction is a **separate architectural choice** about *where* business logic should live, not a POJO-vs-not-POJO distinction.

#### Beyond the video

- **POJO vs JavaBean:** a JavaBean is a POJO that *additionally* follows specific conventions: a no-arg constructor, private fields with public `getX`/`setX`/`isX` (for booleans) accessors, and (traditionally) `Serializable`. Every JavaBean is a POJO; not every POJO is a JavaBean.
- **Anemic Domain Model** is actually named and criticized as an **anti-pattern** by Martin Fowler: putting all logic in "service" classes and leaving entities as pure data bags can violate encapsulation and object-oriented principles (logic that manipulates a `Student`'s state arguably belongs on `Student`, not scattered across service classes). The rich model is generally considered better OOP design, though the anemic style remains common and pragmatic (e.g. simple CRUD apps, DTOs).
- **DTOs (Data Transfer Objects)** are a related-but-distinct concept: POJOs specifically used to transfer data across layers/network boundaries (e.g. API request/response bodies), often intentionally kept anemic even in an otherwise rich-domain-model codebase.
- Modern Java **`record`** types (Java 16+) provide a compact, immutable alternative for pure data carriers: `record Student(String name, int age) {}` auto-generates a constructor, accessors, `equals()`, `hashCode()`, and `toString()` — ideal for anemic-style POJOs/DTOs, though records are immutable (no setters) and cannot extend another class.



### 8. Interview questions

| Question | Answer |
|---|---|
| Why can a `.java` file have only one public class? | So the JVM/compiler has an unambiguous class to treat as the file's public entry point, avoiding ambiguity about where `main` lives. |
| Why must the public class name match the file name? | So the JVM can locate the compiled class directly from the file name (`Demo.java` → `Demo.class`) without scanning file contents. |
| Can a `.java` file have zero public classes? | Yes — all classes can be package-private; there's no requirement to have exactly one public class. |
| Why is `main` both `public` and `static`? | `static` so the JVM can call it without constructing an instance; `public` so it's accessible to the JVM launcher from outside the package. |
| What is a wrapper class? | A class (`Integer`, `Double`, etc.) that wraps a primitive value as an object, enabling it to participate in collections, generics, and object-oriented features. |
| Why do primitives still exist if wrappers cover everything? | Legacy (attract C/C++ developers) and performance (primitives avoid object overhead and heap allocation). |
| What is autoboxing? What is unboxing? | Autoboxing: primitive → wrapper (compiler inserts `valueOf`). Unboxing: wrapper → primitive (compiler inserts e.g. `intValue()`). |
| Where does autoboxing/unboxing occur? | Assignments, method calls (arguments/parameters), and arithmetic operations. |
| Why can unboxing throw NullPointerException? | Because unboxing calls a method (`intValue()`) on the wrapper object; if that object is `null`, the method call fails. |
| Does `==` compare values or references for objects? | References (memory addresses) — never the underlying data, for any reference type including wrapper classes. |
| Why does `Integer a = 100, b = 100; a == b` return true, but `200 == 200` return false? | The Integer cache (-128 to 127) returns the same cached object for values in that range, so `==` accidentally succeeds; outside that range, new objects are created each time. |
| How should you correctly compare two `Integer` objects? | Use `.equals()` (or unbox both with `.intValue()` and compare with `==`), never `==` on the objects directly. |
| Does the Integer cache apply to other wrapper types? | Yes — `Byte`, `Short`, `Long` (-128 to 127), `Character` (0-127), `Boolean` (`TRUE`/`FALSE`). |
| Can abstract classes have constructors? | Yes, they run via `super()` during subclass instantiation. |
| Can an abstract class be declared `final`? | No — contradictory: `final` blocks subclassing, `abstract` requires it. |
| Can an abstract class have static methods or fields? | Yes — statics belong to the class, not an instance, so instantiation is irrelevant. |
| Can an abstract method be `private`? | No — a private method is invisible to subclasses, but abstract methods must be overridable by subclasses. |
| Can an abstract method be `final`? | No — `final` blocks overriding, `abstract` requires it. |
| Can a `private` or `final` method inside an abstract class be non-abstract? | Yes, freely — the restriction is only on making that specific method itself abstract. |
| Can an abstract class have zero abstract methods? | Yes — it only needs `abstract` to prevent direct instantiation; all its methods can be fully implemented. |
| What is a POJO? | A plain Java class with no framework-imposed structure: no required base class, interface, or annotations — typically fields, constructor, getters/setters. |
| Difference between anemic and rich domain models? | Anemic: pure data (fields + getters/setters). Rich: also contains business logic relevant to the entity. Both remain POJOs. |
| POJO vs JavaBean? | A JavaBean is a POJO that follows specific conventions (no-arg constructor, `getX`/`setX` naming, serializability); not all POJOs are JavaBeans. |
| Is the anemic domain model considered good design? | It's debated — Martin Fowler calls it an anti-pattern in OOP terms (violates encapsulation by separating data from behaviour), though it remains common for DTOs and simple CRUD use cases. |



### 9. Summary / mental model

```
FILE RULE          one public top-level type per file; its name == file name
                    (lets the compiler/JVM find the entry point unambiguously)

WRAPPER CLASSES     int → Integer, double → Double, ... (object form of each primitive)
                    exist for: collections/generics (need objects) + OOP features
PRIMITIVES EXIST    for: legacy (C/C++ familiarity) + performance (no object overhead)

AUTOBOXING          primitive → wrapper   (compiler inserts Integer.valueOf(x))
UNBOXING            wrapper → primitive   (compiler inserts x.intValue())
                    triggers: assignments, method calls, arithmetic
                    danger: unboxing a null wrapper → NullPointerException

==                  compares REFERENCES for objects, VALUES for primitives
.equals()           compares VALUES (when properly overridden, e.g. by Integer)
INTEGER CACHE       -128 to 127 are pre-created and reused → == "accidentally" works there
                    ALWAYS use .equals() for wrapper comparisons

ABSTRACT CLASS      cannot instantiate; CAN have constructors, statics, private/final
                    CONCRETE methods; CANNOT have private/final/static ABSTRACT methods
                    (those modifiers contradict "must be overridden")

POJO                plain class, no framework constraints
                    anemic  = data only (fields + getters/setters)
                    rich    = data + business logic
                    both are POJOs — the split is architectural, not definitional
```

**Remember:**
1. The one-public-class-per-file rule exists so the compiler and JVM can unambiguously find the entry point.
2. Wrapper classes bridge primitives into the object world; primitives remain for legacy and performance reasons.
3. Autoboxing/unboxing is compiler-inserted convenience — but unboxing a `null` wrapper throws NPE at runtime, not compile time.
4. `==` never compares values for objects; the Integer cache makes small-number comparisons misleadingly "work" — always use `.equals()`.
5. Apply first-principles reasoning to any abstract-class question: does the combination of modifiers logically contradict "must be overridden by a subclass"?
6. POJO just means framework-free; anemic vs rich domain model is a separate design choice about where business logic lives.

> **Next topics** the video points to: a deeper dive into interfaces (multiple inheritance via interfaces), nested classes, and — implied by POJO discussion — frameworks like Spring/Spring Boot and their use of model classes.


## 19. Nested Classes: Static Nested, Inner, Local, Anonymous




### 0. Corrections to the video (read first)

| Video says | Accurate version |
|---|---|
| "Old Java doesn't allow static members in inner classes; Java 16 changed this" | Precisely: **JEP 395 / JLS updates delivered in Java 16** lifted the restriction, allowing **static members in inner classes** as long as they are compile-time **constants** (`static final` with a constant value) freely, and other statics too — the restriction on arbitrary static members in a non-static inner class was relaxed starting Java 16. Before that, only `static final` **constant** fields were allowed in inner classes, not general static methods/fields. |
| "Local class object outliving its method is a rare edge case" | It's not an edge case — it's the **entire reason lambdas, Runnable, and Comparator instances routinely outlive the method that created them** (e.g. returning a `Runnable` from a factory method, or storing a `Comparator` as a field). This pattern is extremely common in real code, especially with functional interfaces and callbacks. |
| "Object class — we'll cover it in a separate video" | Correctly deferred, but worth noting now: `Object` is the implicit superclass of **every** class in Java, providing `equals()`, `hashCode()`, `toString()`, `getClass()`, `wait()`/`notify()`, and `clone()`. |
| Anonymous classes replaced by lambdas | True **only for functional interfaces** (exactly one abstract method — `Runnable`, `Comparator`, `ActionListener`). Anonymous classes are still necessary when you need to override **multiple methods**, need instance state beyond captured variables, or need a supertype that isn't a functional interface (e.g., extending an abstract class with several methods, or implementing a multi-method interface). |
| "Effectively final" applies only to local classes | It applies to **any variable captured by any inner context**: local classes, anonymous classes, **and lambda expressions** — all three capture local variables the same way and are bound by the same rule. |



### 1. Why nest a class at all?

A **nested class** is a class defined inside another class (or inside a method/block). The enclosing class is the **outer** class; the nested one is the **inner** class (informally — the term "inner class" also has a specific technical meaning, see §3).

```java
class Outer {
    class Inner { }     // nested inside Outer
}
```

Nesting is arbitrarily deep (`Outer` → `Inner` → `Inner2` → ...), but most real code stays at one level.

#### Two reasons to nest a class

**1. Logical grouping.** Some classes only make sense *inside* another — they represent a concept that has no independent identity outside its owner.

```java
class BankAccount {
    private double balance;

    static class InterestCalculator {           // only meaningful inside BankAccount
        static double calculateYearly(double principal, double rate) {
            return principal * rate;
        }
    }
}
```

`InterestCalculator` is a helper that exists purely to serve `BankAccount`; nobody outside needs it as a standalone concept.

**2. Better access to the outer class.** A nested class can access the outer class's **private members directly**, without needing getters/setters — because it is compiled as if it were a member of that class (see §2.3 for the exact mechanism).

```java
class Outer {
    private int x = 4;
    class Inner {
        void print() { System.out.println(x); }   // direct access to Outer's private field
    }
}
```



### 2. The four kinds of nested classes

| Kind | Declared | Belongs to | Key trait |
|---|---|---|---|
| **Static nested class** | `static class Inner { }` inside a class | The **outer class itself** | Behaves like a normal top-level class; no outer instance needed |
| **Inner class** (non-static) | `class Inner { }` inside a class, no `static` | An **outer instance** | Holds an implicit reference to the enclosing object |
| **Local class** | A class declared inside a method/constructor/block | That block's execution | Scoped to the block; can access effectively-final locals |
| **Anonymous class** | A class expression with no name, usually while instantiating | Wherever it's declared | One-off, single-use, often replaced by lambdas today |



### 3. Static nested classes

Declared with `static` inside the outer class. Because it's `static`, it is tied to the **class**, not to any outer instance — exactly like a static field or method.

```java
class Outer {
    static class Inner {
        void greet() { System.out.println("Hello"); }
    }
}
```

#### Creating an instance — no outer instance required

```java
Outer.Inner inner = new Outer.Inner();     // class name is Outer.Inner
inner.greet();                              // Hello
```

The **fully qualified class name** is always `Outer.Inner` (`OuterClass.InnerClass`), because the class isn't visible at the top level of the package.

#### What it can and cannot access

| Outer member | Accessible from static nested class? |
|---|---|
| Static fields / methods (any access modifier) | Yes |
| Non-static (instance) fields / methods | **No — directly** |

```java
class Outer {
    private static int x = 4;   // static: OK to access
    private int y;              // non-static: NOT directly accessible

    static class Inner {
        void print() {
            System.out.println(x);          // OK
            // System.out.println(y);       // ERROR: non-static field y cannot be referenced from a static context
        }
    }
}
```

**Why:** the static nested class can be instantiated without ever creating an `Outer` instance, so there is no `y` to refer to — it belongs to a specific object that may not exist. This is the same logic as "a static method can't use instance fields," applied to a whole class.

**Workaround — explicit outer reference:** pass an `Outer` reference (as a constructor parameter or method parameter) so the static class can reach instance members through it.

```java
static class Inner {
    private final Outer outer;
    Inner(Outer outer) { this.outer = outer; }
    void print() { System.out.println(outer.y); }   // access through an explicit reference
}
```

#### Static nested classes behave like normal classes

They can:
- have fields, methods, constructors of any access modifier (`private`, `protected`, `public`),
- have their own `static` fields and methods (unlike inner classes, see §4.6),
- `extends` a class or `implements` an interface,
- be marked `private` to hide the class entirely from outside `Outer` (a very useful pattern, see below).

```java
class BankAccount {
    private double balance;

    private static class InterestCalculator {    // hidden from the outside world entirely
        static double calculateYearly(double principal, double rate) { return principal * rate; }
    }

    public void computeInterest(double principal) {
        double interest = InterestCalculator.calculateYearly(principal, 0.09);
    }
}
```

Making the nested class `private` means it is a **pure implementation detail** — only methods inside `BankAccount` can use it. This is one of the most valuable patterns for static nested classes: a helper class that has no meaning outside its owner is completely hidden from external code.

#### Use cases

1. **Helper/utility class for the outer class** — the pattern above.
2. **Builder Design Pattern** — the canonical use of a static nested class (`Person.Builder`).
3. **When you need static methods/fields inside a nested class** — only static nested classes allow this (traditionally; see the Java 16 note in §0).
4. Modelling **DTOs / request-response wrappers** in frameworks like Spring Boot — commonly implemented as static nested classes.

#### Beyond the video: singleton via static nested class (lazy holder idiom)

```java
public class Singleton {
    private Singleton() { }
    private static class Holder {
        static final Singleton INSTANCE = new Singleton();
    }
    public static Singleton getInstance() { return Holder.INSTANCE; }
}
```

`Holder` only loads (and creates `INSTANCE`) the first time `getInstance()` is called — lazy initialization, thread-safe without any explicit synchronization, relying on the JVM's class-initialization guarantees.



### 4. Inner classes (non-static)

A nested class **without** `static`. It belongs to an **instance** of the outer class, not the class itself.

```java
class Outer {
    class Inner {
        void greet() { System.out.println("Hello"); }
    }
}
```

#### Creating an instance — outer instance required first

```java
Outer outer = new Outer();
Outer.Inner inner = outer.new Inner();      // must go through an outer instance
inner.greet();
```

Syntax breakdown: `outer.new Inner()` — take the outer object, then create an `Inner` **inside it**. This is the same "dot means look inside the object" logic as accessing a field (`outer.x`) or method (`outer.someMethod()`), just extended to constructing a nested object.

**Shorthand (single expression):**
```java
Outer.Inner inner = new Outer().new Inner();
```
Only useful when you don't need to keep a reference to the outer instance separately.

#### Memory representation

```
STACK                          HEAP
┌─────────────┐                ┌────────────────────────┐
│ outer  ────────────────────► │ Outer object            │
├─────────────┤                └────────────────────────┘
│ inner  ────────────────────► ┌────────────────────────┐
└─────────────┘                │ Inner object            │
                                │  [implicit Outer ref] ──┼──► points back to the Outer object above
                                └────────────────────────┘
```

- The `Inner` object gets its **own separate heap allocation** — it is **not** stored inside the `Outer` object.
- Every inner-class instance **implicitly holds a reference to its enclosing outer instance** (compiler-generated, conventionally accessed as `Outer.this` inside the inner class). This is exactly how the inner class can reach the outer instance's members.
- Each new `Outer` instance that creates its own `Inner` gets a **distinct** `Inner` object bound to it — unlike a static nested class, where there's no such 1:1 binding (a static nested class isn't tied to any outer instance at all).

#### What it can access

Inner classes have **no restriction**: they can access **any** member of the outer class — static or non-static, `private`, `protected`, or `public` — because they only exist bound to a specific outer instance, so there's no ambiguity about *whose* instance data they're reading.

```java
class Outer {
    private int x = 10;
    class Inner {
        int x = 20;
        void print() {
            System.out.println(x);              // 20 — inner's own field shadows outer's
            System.out.println(Outer.this.x);    // 10 — explicit access to the outer instance's field
        }
    }
}
```

**Shadowing rule:** if the inner class declares a field with the same name as the outer class's field, the inner one **wins by default** (shadows the outer one). To reach the shadowed outer field, use `Outer.this.fieldName` — `Outer.this` gives you an explicit reference to the enclosing instance (analogous to how `super` gives access to a hidden parent-class member, but for enclosing instances rather than superclasses).

#### Beyond the video

- **Static members in inner classes:** disallowed in older Java because of a genuine ambiguity — if `Inner.x` were `static`, is `x` shared across **all** `Outer` instances (like a normal static member, "belongs to the class"), or does each `Outer` instance get its own copy of `Inner`'s static state (since each `Outer` instance has its own `Inner` instance)? Java resolved this ambiguity historically by **simply disallowing** static members (except compile-time constants) in inner classes. **Java 16+** relaxed this restriction.
- **Memory leak risk:** because every inner-class instance holds a reference back to its outer instance, an inner-class object that outlives its intended use (e.g. registered as a long-lived listener) can **prevent the outer instance from being garbage collected**. This is a classic real-world bug, especially with Android `Activity` subclasses and anonymous inner listener classes. Prefer **static nested classes** whenever you don't actually need the outer instance's data.
- Inner classes **cannot declare static members** other than compile-time constants (pre-Java 16), but they behave like a normal class otherwise: can extend a class, implement interfaces, have any access modifier, have constructors, etc.



### 5. Local classes

A class declared **inside a method, constructor, or any block** (`if`, `for`, `while`, `switch`, a static initializer block, etc.) — its scope is limited to that block.

```java
class Outer {
    void greet() {
        class Local {                        // declared inside a method
            void sayHello() { System.out.println("Hello"); }
        }
        Local local = new Local();
        local.sayHello();                    // Hello
    }
}
```

- The class can be declared in **any** code block, not just methods: inside `if`, loops, `switch`, constructors, static blocks.
- It behaves like an inner class regarding access: it can use **any** member of the enclosing class (static or non-static), because a local class is compiled as if non-static (belongs to a specific execution context).

#### Beyond the video: how "escaping" a local class actually works in modern Java

The video demonstrates returning a local-class instance via a reference typed `Object` (since the local class's name isn't visible outside the method). Realistically, this pattern almost always uses a **named supertype**, not raw `Object`:

```java
interface Greeter { void sayHello(); }

Greeter makeGreeter(String message) {
    class LocalGreeter implements Greeter {
        @Override public void sayHello() { System.out.println(message); }
    }
    return new LocalGreeter();          // returned as Greeter, not as LocalGreeter or Object
}

Greeter g = makeGreeter("Hi there");
g.sayHello();                            // works fine — Hi there
```

This is exactly how local classes (and anonymous classes, and lambdas) are used in practice — as an implementation of an interface or abstract class, returned or stored via that supertype's reference. Using bare `Object` (as a fallback with no common supertype) is a much rarer, more contrived scenario.

#### The effectively-final rule

A local class (and an anonymous class, and a lambda) can use a local variable from its enclosing scope **only if that variable is `final` or "effectively final"** — meaning it is **never reassigned** after initialization, whether or not you write `final` explicitly.

```java
void greet() {
    int y = 5;
    class Local {
        void print() { System.out.println(y); }    // OK: y is effectively final
    }
    // y++;                                          // if uncommented: COMPILE ERROR
    //   "local variables referenced from an inner class must be final or effectively final"
}
```

#### Why this rule exists

```
STACK (during greet())              HEAP
┌──────────────┐                    ┌────────────────────┐
│ y = 5        │                    │ Local object         │
│ local ───────┼──────────────────► │  [copy of y = 5]     │
└──────────────┘                    └────────────────────┘
```

- `y` is a **local variable**, which lives on the **stack**, scoped to the method call. It disappears when `greet()` returns.
- But the `Local` object (created on the heap) might **outlive** the method — e.g. if a reference to it is returned or stored elsewhere.
- To make this safe, the **compiler copies the local variable's value into the inner object itself** (as a synthetic field) at the moment the object is created — so the nested class has its own independent copy that survives after the enclosing method ends.
- If `y` could be reassigned after the copy is made, there would be an unresolvable ambiguity: which value should the copy reflect — the value at creation time, the latest value, or something else? To avoid this ambiguity entirely, Java **requires the captured variable to never change** — hence "effectively final."

**Applies identically to:** local classes, anonymous classes, and lambda expressions — all three capture enclosing local variables the same way and are bound by the same effectively-final rule.

```java
int count = 0;
Runnable r = () -> System.out.println(count);   // OK: count is effectively final
// count++;                                       // uncommenting this breaks the lambda above too
```

#### Beyond the video

- The captured copy is exactly why this is called "capture by value" — the lambda/local/anonymous class doesn't see live updates to the original variable; it sees a frozen snapshot from the moment of capture.
- Instance fields and static fields of the enclosing class are **not** subject to this rule — only **local variables and method parameters** are, because only those live on the stack with a scope tied to the method call.
- Local classes are rarely used directly in production; much of what they'd be used for is now more naturally expressed with lambdas or by extracting a proper named class.



### 6. Anonymous classes

A class **without a name**, declared and instantiated in a single expression — used for a one-off implementation that will only be needed once.

#### The problem it solves

```java
class Person {
    void introduce() { System.out.println("Hi, I am a person"); }
}
```

Without anonymous classes, overriding `introduce()` just once (a single special case) requires a full named subclass:

```java
class Guest extends Person {
    @Override void introduce() { System.out.println("Hi, I am a guest"); }
}

Person p2 = new Guest();
p2.introduce();       // Hi, I am a guest
```

That's a lot of ceremony (a whole new file/class) for something you'll only ever use once.

#### The anonymous class syntax

```java
Person p2 = new Person() {                        // no semicolon here — a class body follows
    @Override
    void introduce() { System.out.println("Hi, I am a guest"); }
};                                                   // semicolon ends the whole statement

p2.introduce();       // Hi, I am a guest
```

- `new Person() { ... }` creates an unnamed subclass of `Person` **and** instantiates it, in one expression.
- Everything inside the `{ }` is the class body — you can override methods, and (with restrictions) add fields and new methods.

#### What's allowed inside an anonymous class

```java
Person p2 = new Person() {
    String name = "Aditya";                              // OK: fields allowed

    void greet() { System.out.println("Hello"); }         // OK: new methods allowed, BUT...

    @Override
    void introduce() {
        greet();                                            // OK: callable from WITHIN the class
        System.out.println("Hi, I am " + name);
    }
};

p2.introduce();     // Hello \n Hi, I am Aditya
// p2.greet();      // ERROR: greet() is undefined for the reference type Person
// p2.name          // ERROR: name is undefined for the reference type Person
```

**Key restriction:** you can add extra fields and methods, but you **cannot call them from outside** through the reference — because the reference's **declared type is still `Person`**, and `Person` doesn't have `greet()` or `name`. You can only call new members **from inside** the anonymous class itself (e.g., from within the overridden method).

**No constructors.** An anonymous class has no name, and a constructor's name must match its class's name — so it's structurally impossible to define one. (Instance initializer blocks `{ ... }` can be used instead if setup logic is needed.)

#### Rules recap

| Can it... | Allowed? |
|---|---|
| Override methods from the supertype | Yes |
| Add new fields | Yes, but only usable from inside the class |
| Add new methods | Yes, but only callable from inside the class (e.g., from an overridden method) |
| Have a constructor | **No** — no name to match |
| Access outer class's static/non-static members | Yes — same rules as an inner class |
| Use enclosing local variables | Yes, if effectively final (same rule as local classes) |

#### When to use it

- A single, throwaway implementation of an interface/abstract class, needed **only once**, in a narrow, localized context.
- **Not** for something you'll reuse — a named class (or a shared instance) is better if the logic repeats.

#### Beyond the video: anonymous classes today (lambdas)

**Since Java 8**, anonymous classes implementing a **functional interface** (exactly one abstract method) are usually replaced by lambdas — much shorter, and avoid a subtle "which `this`" trap.

```java
// Anonymous class version
Runnable r1 = new Runnable() {
    @Override public void run() { System.out.println("Running"); }
};

// Lambda version — equivalent, far more concise
Runnable r2 = () -> System.out.println("Running");
```

**Anonymous classes remain necessary when:**
- The supertype has **more than one abstract method** (lambdas only work for functional interfaces),
- You need to extend an **abstract class** with multiple methods to override,
- You need genuine **instance state** with more complex initialization than a lambda's captured variables allow,
- You need `this` to refer to the anonymous class instance itself (a lambda's `this` refers to the **enclosing** instance, not the lambda — a real behavioural difference, not just style).

```java
// Multiple abstract methods → must use anonymous class, lambda can't do this
Comparator<String> cmp = new Comparator<>() {
    @Override public int compare(String a, String b) { return a.length() - b.length(); }
    // Comparator also has default/static methods, but only one ABSTRACT method (compare) —
    // this specific example COULD be a lambda; use anonymous classes for genuinely
    // multi-abstract-method types (rare in practice since most are functional interfaces)
};
```



### 7. Comparing all four kinds

| | Static Nested | Inner (non-static) | Local | Anonymous |
|---|---|---|---|---|
| Tied to | The outer **class** | An outer **instance** | The enclosing **block/method** | Wherever declared (expression) |
| Needs outer instance to create? | No | Yes (`outer.new Inner()`) | No (created like a normal local class) | No |
| Can have static members | Yes (freely) | No (pre-Java 16; constants only) | No | No |
| Can access outer's non-static members directly | No (needs an explicit reference) | Yes | Yes | Yes |
| Can have a name-based constructor | Yes | Yes | Yes | **No** |
| Effectively-final rule on captured locals | N/A (no enclosing method locals) | N/A | **Yes** | **Yes** |
| Typical modern use | Helper classes, Builder pattern, DTOs | Rare; risk of memory leaks | Very rare | Rare now — mostly replaced by lambdas for functional interfaces |



### 8. How rare is each, in practice

| Kind | Real-world frequency |
|---|---|
| **Static nested class** | **Most common** — used all the time (helper classes, builders, DTOs) |
| **Inner class** | Occasional — used when genuinely need outer-instance binding |
| **Anonymous class** | Rare today — mostly displaced by lambdas since Java 8, but still needed for multi-method supertypes |
| **Local class** | **Rarest** — you will seldom see or need one in production code |



### 9. Interview questions

| Question | Answer |
|---|---|
| What are the four kinds of nested classes in Java? | Static nested, inner (non-static), local, and anonymous. |
| Why nest a class at all? | Logical grouping (a class only meaningful inside another) and better access to the outer class's private members without getters/setters. |
| How do you instantiate a static nested class? | `Outer.Inner obj = new Outer.Inner();` — no outer instance needed. |
| How do you instantiate a (non-static) inner class? | `Outer.Inner obj = outer.new Inner();` — requires an existing outer instance. |
| Can a static nested class access the outer class's instance (non-static) fields directly? | No — it isn't tied to any instance. It can only do so if given an explicit reference to an `Outer` object. |
| Can an inner class access the outer class's private members? | Yes — freely, both static and non-static, because it's implicitly bound to a specific outer instance. |
| Why couldn't inner classes have static members historically? | Ambiguity: is the static member shared per class (one copy) or per outer instance (since each outer instance has its own inner instance)? Java resolved this by disallowing it (except constants); relaxed in Java 16+. |
| What is the memory-leak risk with inner classes? | Every inner-class instance holds an implicit reference to its outer instance, so a long-lived inner object can prevent the outer instance from being garbage collected. |
| What is `Outer.this`? | An explicit reference to the enclosing instance, used to access an outer member that's shadowed by an inner one with the same name. |
| What is a local class? | A class declared inside a method, constructor, or any code block; scoped to that block. |
| What is the effectively-final rule? | A local variable used inside a local/anonymous class or lambda must never be reassigned after initialization — because the compiler copies its value into the inner object, and reassignment would create ambiguity about which value to copy. |
| Why does the effectively-final rule exist? | The local variable lives on the stack and disappears when the method ends, but the inner object might outlive the method — so its value is copied in at creation time; allowing later changes would make that copy's correctness ambiguous. |
| What is an anonymous class? | A nameless class declared and instantiated in a single expression, typically for a one-off override. |
| Can an anonymous class have a constructor? | No — a constructor's name must match the class name, and an anonymous class has no name. |
| Can you call a new method added inside an anonymous class from outside? | No — the reference's declared type is the supertype, which doesn't know about the new method; it's only callable from inside the anonymous class itself. |
| Have lambdas replaced anonymous classes? | Only for functional interfaces (single abstract method). Anonymous classes are still needed for multi-method interfaces/abstract classes, or when you need the anonymous instance's own `this`. |
| Which nested class type is used most in production? | Static nested classes, by a wide margin. |



### 10. Summary / mental model

```
STATIC NESTED   → belongs to the CLASS.        Outer.Inner obj = new Outer.Inner();
                  Can't touch outer's instance members directly.
                  Most commonly used (helpers, Builder pattern, DTOs).

INNER (non-static) → belongs to an INSTANCE.    Outer.Inner obj = outer.new Inner();
                  Can touch ANY outer member (implicit outer reference held internally).
                  Watch for memory leaks (outer kept alive via inner's reference).

LOCAL           → declared inside a method/block. Scoped to that block.
                  Can use enclosing locals ONLY if effectively final.
                  Rarest in practice.

ANONYMOUS       → no name; declared + instantiated in one expression.
                  One-off override; new members only callable from inside.
                  No constructor possible.
                  Mostly replaced by LAMBDAS for functional interfaces since Java 8.

EFFECTIVELY FINAL RULE (local, anonymous classes, AND lambdas):
  a captured local variable must never be reassigned —
  the compiler copies its value in at creation time; reassignment → ambiguity → compile error.
```

**Remember:**
1. Nest a class when it's logically meaningless outside its owner, or needs private access to the owner.
2. Static nested = tied to the class; Inner = tied to an instance and carries an implicit reference back to it.
3. Inner classes risk memory leaks by keeping the outer instance alive.
4. Effectively-final governs any variable a local class, anonymous class, or lambda captures from its enclosing scope.
5. Anonymous classes can't have constructors and can't expose new members through the supertype reference.
6. In modern code: static nested classes are common; anonymous classes are increasingly replaced by lambdas; local classes are rare.

## 20. Java Standard I/O: System.out, Streams, BufferedReader, Scanner




### 0. Corrections to the video (read first)

| Video says | Accurate version |
|---|---|
| "`System.out` is of type `PrintStream`, call it `x`" | Correct once resolved: `public static final PrintStream out`. No correction needed beyond confirming the exact declaration in `java.lang.System`. |
| "`System.in` is `InputStream` type, concretely `BufferedInputStream`" | Correct in modern JDKs: `System.in` is a `BufferedInputStream` wrapping the OS-level input, referenced via the `InputStream` type. The video calls the exact concrete class "not important to know," which is fine, but it *is* buffered by the JVM already at the OS-interaction level — the extra buffering added by wrapping it in `BufferedReader` is about avoiding repeated native/JVM-boundary calls per **character read via a `Reader`**, not about the OS call itself being unbuffered. |
| "Reads one byte at a time is a JVM limitation" | More precisely: `InputStream.read()` (no-arg) is specified to read and return **a single byte** (or -1 at end of stream) — this is the method's contract, not an incidental JVM restriction. Overloads exist — `read(byte[])` and `read(byte[], int, int)` — that read multiple bytes into a buffer in one call, so raw `InputStream` was never strictly limited to one byte per call; the video's manual loop uses only the simplest overload. |
| "Character takes 2 bytes because of Unicode" | More precise: Java's `char` is a 16-bit UTF-16 code unit (2 bytes), enough for the **Basic Multilingual Plane**; characters outside it (many emoji, some rare scripts) require a **surrogate pair** — two `char`s (4 bytes) to represent one visual character/code point. |
| "Scanner is slower than BufferedReader because of tokenizing, regex, and internal type conversion" | All correct and the standard explanation. **Beyond that:** `Scanner` is also **not thread-safe** and, unlike `BufferedReader`, wraps `InputStreamReader`/decoding logic with additional pattern-matching machinery per token, which is the dominant cost in tight loops (e.g., competitive programming reading 10^5+ integers). |
| `System.in`/`System.out` are always console | By default yes, but **`System.setIn(...)` / `System.setOut(...)` / `System.setErr(...)`** can redirect these streams to any `InputStream`/`PrintStream` at runtime (e.g. for testing, or redirecting output to a file) — worth knowing since it explains how frameworks capture console I/O in tests. |



### 1. Types of I/O by source/destination

| Type | Example |
|---|---|
| **Console I/O** | Keyboard input, `System.out.println()` to the screen |
| **File I/O** | Reading/writing files on disk |
| **Network I/O** | API calls, client-server communication over sockets/HTTP |
| **Memory I/O** | Reading/writing to an in-memory buffer |

This set of notes focuses on **console I/O**. File and network I/O are covered separately.



### 2. Deconstructing `System.out.println()`

```java
System.out.println("Hello");
```

Reading this **first-principles**, piece by piece:

- `println` has parentheses and takes an argument → it's a **method**.
- `out` is lowercase → it's a **variable** (specifically, a reference variable), not a class.
- `System` is capitalized → it's a **class**.
- Calling `System.out` (`ClassName.variableName`) means `out` must be a **`static`** field of `System` — that's the only way to access it without an object.

Putting it together:

```java
public final class System {
    public static final PrintStream out = ...;   // a static, final, public field
    public static final PrintStream err = ...;
    public static final InputStream in  = ...;
}
```

- `System` is a real class, part of **`java.lang`** (auto-imported into every Java file — this is why you never need to write `import java.lang.System;`).
- `out` is declared `public static final PrintStream`. `static` → accessible via the class name. `final` → the reference can never be reassigned. `public` → accessible from anywhere.
- `PrintStream` is itself a real class (in `java.io`), with methods `println(String)`, `print(String)`, and `printf(...)`.

So `System.out.println("Hello")` means: *"Go to the static `out` field on the `System` class (a `PrintStream` object), and call its `println` method with the string `"Hello"`."*

#### `println` vs `print` vs `printf`

| Method | Behaviour |
|---|---|
| `println(...)` | Prints the argument, then appends a **newline** |
| `print(...)` | Prints the argument, **no newline** — next output continues on the same line |
| `printf(...)` | Prints using a **format string** (e.g., `%d`, `%.2f`) for controlled/formatted output |

#### Why is `out` `static`?

If it weren't, you'd need to manually instantiate `System` yourself first:

```java
System s = new System();          // hypothetical, if out were non-static
s.out.println("Hello");           // extra, unnecessary step every time
```

By making `out` `static`, Java lets every program access it directly as `System.out`, with zero setup — a deliberate convenience, exactly the same reasoning as why `main` is `static`.



### 3. `System.err`

```java
public static final PrintStream err = ...;    // same type, same declaration style, different purpose
```

`System.err` is another `PrintStream` object, functionally identical to `System.out` (same `println`/`print`/`printf` methods) but conventionally used for **error output**.

```java
int age = -3;
if (age < 0) {
    System.err.println("Invalid age");   // signals: this is an error message
} else {
    System.out.println("Age: " + age);   // normal program output
}
```

**Why separate them?**
1. **Separation of concerns**: readers of the code (and logs) can immediately tell error output from normal output.
2. **OS-level distinction**: `System.err` is typically mapped to a separate output stream at the OS level (`stderr` vs `stdout`), so tools can **redirect or filter** them independently — e.g., showing errors in red in a terminal, or writing errors to a separate log file, without touching normal output.

There's no hard rule enforcing this split — you *can* use `System.out` for everything — but it's a widely followed convention. In modern applications, raw console printing is usually replaced entirely by a **proper logging framework** (e.g., SLF4J/Logback, Log4j2) which formalizes this separation (`INFO`, `WARN`, `ERROR` levels) with far more control.



### 4. Streams: the foundation of Java I/O

A **stream** is simply a **flow of data** — imagine data moving through a pipe.

| Stream type | Direction |
|---|---|
| **Input stream** | Data flows **into** the program (e.g., reading user input) |
| **Output stream** | Data flows **out of** the program (e.g., printing to the screen) |

Java's entire I/O system is **stream-based**: whether reading from console, file, network, or memory, you always work through some kind of stream abstraction.

#### `InputStream` and `OutputStream`: the root abstract classes

```java
abstract class InputStream  { abstract int read();  }   // (conceptually)
abstract class OutputStream { abstract void write(int b); }
```

- Both are **abstract classes** — they declare `read()`/`write()` but don't define *how* reading/writing actually happens for a specific source. That's left to their subclasses (`FileInputStream`, `FileOutputStream`, and so on) — a direct application of abstraction: separating **what** (read/write capability) from **how** (file-specific, console-specific, network-specific implementation).
- Both are **streams of bytes**. Reading a character like `'A'` returns its **ASCII/Unicode code point as a byte** (e.g., 65 for `'A'`).

#### Hierarchy

```
InputStream (abstract)                    OutputStream (abstract)
 ├── FileInputStream                       ├── FileOutputStream
 ├── ByteArrayInputStream                  ├── ByteArrayOutputStream
 ├── BufferedInputStream                   ├── BufferedOutputStream
 └── DataInputStream                       ├── DataOutputStream
                                            └── PrintStream    ← System.out / System.err
```

`System.out` and `System.err` are of type `PrintStream`, which extends `OutputStream` (through the hierarchy) and overrides `write()` to know how to send bytes to the console.

`System.in` is of type `InputStream`, concretely a `BufferedInputStream` internally — but referenced via the `InputStream` type. By default, it's wired to read from the **keyboard**.



### 5. Reading input the primitive way: `System.in.read()`

```java
import java.io.IOException;

public class Demo {
    public static void main(String[] args) throws IOException {
        int x = System.in.read();
        System.out.println(x);
    }
}
```

- `System.in.read()` returns an **`int`** — the byte value read (or -1 at end of stream), not a `char`. If you type `A`, it returns `65` (its ASCII value).
- `throws IOException` is required because `read()` can throw a checked exception (covered properly under exception handling).
- To get the actual character back: `char c = (char) System.in.read();` — an explicit cast.

#### The critical limitation: reads only ONE byte at a time

```java
char c = (char) System.in.read();
System.out.println(c);
```

If you type `Aditya` and press Enter, this prints only `A` — the rest (`ditya`) is left unread, sitting in the **input buffer**.

**Why:** each call to `read()` consumes exactly one byte from the stream. Reading a full word/string requires **calling `read()` in a loop**, checking each byte until you hit the newline (`\n`, byte value for Enter):

```java
StringBuilder sb = new StringBuilder();
int c;
while ((c = System.in.read()) != '\n') {
    sb.append((char) c);
}
System.out.println(sb.toString());
```

#### Beyond the video: what's actually happening

```
Keyboard → OS input buffer → System.in.read() → your program (one byte at a time)
```

When you type `Aditya` + Enter, the OS buffers it as bytes: `65, 100, 105, 116, 121, 97, 10` (ASCII for `A, d, i, t, y, a, \n`). Each call to `System.in.read()` fetches exactly the next byte from that buffer. To read the whole word, your program must repeatedly call `read()`, checking for the terminating newline — hence the loop above.

This is extremely verbose and slow for real programs — every character means a potential system-boundary interaction — which motivates everything that follows.



### 6. `Reader`: a stream of characters instead of bytes

Java introduced a **second hierarchy**, parallel to `InputStream`/`OutputStream`, that works directly with **characters** instead of raw bytes — removing the need for manual byte-to-char casting.

```
Reader (abstract)                Writer (abstract)
 ├── BufferedReader                ├── BufferedWriter
 ├── InputStreamReader              ├── OutputStreamWriter
 └── FileReader                     └── FileWriter
```

- `Reader` is abstract, declares `read()`/`read(char[])`, and works with a **stream of characters**, not bytes.
- Its key subclasses for console I/O: **`InputStreamReader`** and **`BufferedReader`**.



### 7. `BufferedReader`: solving the "one byte at a time" problem

#### The problem it solves

`System.in.read()` makes an **OS system call for every single byte**. Reading a string of 1000 characters means **1000 separate OS calls** — extremely slow.

#### How `BufferedReader` fixes this

`BufferedReader` introduces its **own buffer inside the JVM's program memory**. Instead of asking the OS for one byte at a time, it reads a **large chunk** from the OS buffer into its own internal buffer in one call, then serves characters to your program **directly from that in-memory buffer** — no repeated OS calls needed for subsequent characters.

```
Keyboard → OS buffer → [BufferedReader's internal buffer, in JVM memory] → program
                          (one bulk read from OS)         (many fast reads from here)
```

This is the same idea as any variable living in memory (`int x = 4;`) — once it's in your program's own memory, accessing it again costs nothing extra; you don't need to go back to its original source.

#### The compatibility problem: `BufferedReader` needs a `Reader`, not bytes

`BufferedReader` works on a **stream of characters**. `System.in` is a stream of **bytes** (`InputStream`). These two are **not directly compatible** — you need something to bridge them.

#### `InputStreamReader`: the bridge

`InputStreamReader` converts a byte stream (`InputStream`) into a character stream (`Reader`), so that `BufferedReader` can then buffer it.

```java
InputStreamReader isr = new InputStreamReader(System.in);   // bytes → characters
BufferedReader br = new BufferedReader(isr);                 // buffers the character stream

String name = br.readLine();          // reads an entire line at once
System.out.println(name);
```

**Or, inline (the far more common style in real code):**
```java
BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
String name = br.readLine();
System.out.println(name);
```

#### Full data flow

```
Keyboard input "Aditya\n"
      ↓
OS converts to bytes: 65, 100, 105, 116, 121, 97, 10
      ↓
System.in (InputStream) — receives raw bytes from OS
      ↓
InputStreamReader — converts stream of bytes → stream of characters: A, d, i, t, y, a
      ↓
BufferedReader — reads this character stream in bulk into its own buffer, serves readLine()
      ↓
readLine() assembles "Aditya" and returns it as a String
```

#### Beyond the video

- `readLine()` returns `null` at end-of-stream (e.g., piped input ends) — always worth checking in loops reading multiple lines.
- `BufferedReader` only reads **`String`**; converting to other types (`int`, `double`) requires manual parsing:
```java
int age = Integer.parseInt(br.readLine());
```
- **Try-with-resources** is the modern, correct way to manage a `BufferedReader` — it implements `AutoCloseable`, and forgetting to close it can leak the underlying stream:
```java
try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
    String name = br.readLine();
    System.out.println(name);
} catch (IOException e) {
    e.printStackTrace();
}
```



### 8. `Scanner`: the modern, simplified way

Introduced in **Java 1.5**, `Scanner` solves `BufferedReader`'s two biggest pain points:

1. **`BufferedReader` only reads `String`** — every other type needs manual parsing (`Integer.parseInt`, etc.).
2. **The setup is verbose** — chaining `InputStreamReader` inside `BufferedReader` just to read console input.

```java
import java.util.Scanner;

Scanner sc = new Scanner(System.in);
String name = sc.nextLine();
System.out.println(name);
```

#### Key facts about `Scanner`

- Lives in **`java.util`**, not `java.io` — it's a **utility class**, not a member of the `InputStream`/`Reader` hierarchies. It **wraps** those hierarchies rather than extending them.
- Its constructor still ultimately needs something to read from — `System.in` (keyboard), a `File` object (for file input), or even a plain `String` (to scan a fixed string as if it were input):

```java
Scanner sc1 = new Scanner(System.in);                 // keyboard
Scanner sc2 = new Scanner(new File("temp.txt"));       // file (throws FileNotFoundException)
Scanner sc3 = new Scanner("10 20 30");                 // scan a fixed string directly
```

- Internally, it's built on the same underlying machinery (`InputStreamReader`/`BufferedReader`-like buffering), but adds **tokenization** (splitting input by whitespace into discrete tokens) and automatic type parsing on top.

#### Tokenization

`Scanner` splits input into **tokens**, delimited by whitespace by default:

```java
// input: "Hello I am Aditya"
// tokenized as: ["Hello", "I", "am", "Aditya"]
```

#### Methods

| Method | Reads |
|---|---|
| `nextLine()` | The entire current line (until Enter) |
| `next()` | The next single **token** (word) — stops at whitespace |
| `nextInt()` | The next token, parsed as `int` |
| `nextDouble()` | The next token, parsed as `double` |
| `nextBoolean()` | The next token, parsed as `boolean` |
| `nextFloat()`, `nextLong()`, `nextShort()`, `nextByte()` | Corresponding primitive types |
| `nextBigDecimal()` | As a `BigDecimal` |

```java
Scanner sc = new Scanner(System.in);

int age = sc.nextInt();          // no manual parsing needed
double gpa = sc.nextDouble();
boolean active = sc.nextBoolean();
```

**`next()` vs `nextLine()`:**
```java
// input: "Aditya Tandon"
sc.next();       // returns "Aditya" only — stops at the space
sc.nextLine();   // returns "Aditya Tandon" — the whole line
```

#### Beyond the video: the classic `nextInt()` + `nextLine()` pitfall

```java
Scanner sc = new Scanner(System.in);
int age = sc.nextInt();          // reads the number, but leaves the trailing '\n' in the buffer
String name = sc.nextLine();     // immediately reads that leftover '\n' as an "empty line" — BUG
```

`nextInt()`/`nextDouble()`/etc. don't consume the newline character after the token. A following `nextLine()` call picks up that leftover newline and returns an empty string instead of waiting for real input. **Fix:** add an extra `sc.nextLine();` to consume the leftover newline, or use `sc.next()` consistently, or read everything as strings and parse manually.

```java
int age = sc.nextInt();
sc.nextLine();                    // consume the leftover newline
String name = sc.nextLine();      // now works correctly
```



### 9. `Scanner` vs `BufferedReader`: performance

| | `BufferedReader` | `Scanner` |
|---|---|---|
| Reads | Only `String` (needs manual parsing for other types) | Directly into any primitive type |
| Internal overhead | Minimal — just buffers characters | Tokenization + regex matching + type parsing on every call |
| Speed | **Faster** | **Slower** (measurably, in tight loops) |
| Package | `java.io` | `java.util` |
| Typical use | Performance-critical code, competitive programming | General-purpose applications |

**Why `Scanner` is slower:** it does considerably more internal work per call — splitting input via **regular expressions**, matching tokens, and converting types — none of which `BufferedReader` does. `BufferedReader` just serves raw characters/lines with no extra processing.

**Practical guidance:** use `Scanner` for typical application code (readability and convenience win). Use `BufferedReader` (often combined with `StringTokenizer` or manual `split()` for parsing) when performance matters — competitive programming judged on strict time limits, or reading very large volumes of input.



### 10. Full comparison table

| | `System.in.read()` | `BufferedReader` | `Scanner` |
|---|---|---|---|
| Reads | One byte at a time | Whole lines (`String` only) | Tokens, with type-specific methods |
| Requires manual parsing for numbers? | Yes (and manual looping for strings) | Yes (`Integer.parseInt`, etc.) | No — built-in `nextInt()`, `nextDouble()`, etc. |
| Buffering | None (raw OS calls) | Yes, internal JVM-memory buffer | Yes, inherited from underlying reader |
| Package | `java.io` | `java.io` | `java.util` |
| Speed | Very slow (one OS call per byte) | Fast | Slower than `BufferedReader` |
| Typical use | Educational / low-level illustration only | Performance-sensitive code | General application code |



### 11. Interview questions

| Question | Answer |
|---|---|
| What type is `System.out`? | `PrintStream`, declared `public static final` in the `System` class. |
| Why is `System.out` static? | So it can be accessed via the class name (`System.out`) without needing to instantiate `System` first. |
| Difference between `System.out` and `System.err`? | Both are `PrintStream` objects with identical methods; `err` is conventionally used for error messages and is mapped to a separate OS-level stream (`stderr`), enabling independent redirection/filtering from normal output. |
| What package is `System` in? | `java.lang`, which is auto-imported into every Java file. |
| What is a stream? | A flow of data, either into the program (input stream) or out of the program (output stream). |
| Are `InputStream`/`OutputStream` byte-based or character-based? | Byte-based — they work with a stream of bytes. |
| What type is `System.in`? | `InputStream` (concretely a `BufferedInputStream` internally), wired by default to read from the keyboard. |
| What does `System.in.read()` return? | An `int` representing the byte value read (or -1 at end of stream) — not a `char` directly; requires an explicit cast. |
| Why can't `System.in.read()` easily read a full word? | It reads only one byte per call; reading a full string requires looping and checking for the terminating character. |
| What problem does `BufferedReader` solve? | Reduces repeated OS-level calls by reading a large chunk of data into an internal JVM-memory buffer at once, serving subsequent reads from memory. |
| Why can't `BufferedReader` wrap `System.in` directly? | `BufferedReader` works with a stream of characters (`Reader`); `System.in` is a stream of bytes (`InputStream`) — they're incompatible without a bridge. |
| What does `InputStreamReader` do? | Converts a byte stream into a character stream, bridging `InputStream` and `Reader`-based classes like `BufferedReader`. |
| What are `Reader`/`Writer`? | The character-stream equivalents of `InputStream`/`OutputStream` — abstract classes for reading/writing character data directly. |
| What package is `Scanner` in? | `java.util`, not `java.io` — it's a utility class that wraps the I/O hierarchy rather than extending it. |
| What does `Scanner` do internally? | Wraps an `InputStream`/`Reader`, tokenizes input by whitespace (using regex), and provides typed methods (`nextInt()`, `nextDouble()`, etc.) that parse tokens automatically. |
| Difference between `next()` and `nextLine()`? | `next()` reads a single whitespace-delimited token; `nextLine()` reads the entire current line. |
| What's the classic `nextInt()`/`nextLine()` bug? | `nextInt()` leaves the trailing newline in the buffer; a following `nextLine()` immediately reads that leftover newline as an empty string instead of waiting for input. |
| Is `Scanner` faster or slower than `BufferedReader`? | Slower — due to tokenization, regex matching, and internal type conversion overhead that `BufferedReader` doesn't do. |
| When would you prefer `BufferedReader` over `Scanner`? | When performance matters — competitive programming, very large input volumes, or tight time constraints. |
| Can `System.out`/`System.in` be redirected? | Yes, via `System.setOut(...)`, `System.setIn(...)`, `System.setErr(...)`, useful for testing or logging to files. |


### 12. Summary / mental model

```
System.out / System.err     → PrintStream objects (static, final, public fields of System)
System.in                    → InputStream object, default source: keyboard

STREAM = flow of data
  InputStream / OutputStream → work with BYTES     (abstract; read()/write() declared, not defined)
  Reader / Writer            → work with CHARACTERS (parallel hierarchy, avoids byte→char casting)

System.in.read()              → one byte at a time, many OS calls, very slow
        ↓ wrap in
InputStreamReader              → converts byte stream → character stream (the "bridge")
        ↓ wrap in
BufferedReader                 → bulk-reads into an internal JVM buffer; readLine() gives whole lines
                                  (String only — manual parsing needed for other types)
        ↓ superseded for convenience by
Scanner (java.util)            → tokenizes input, gives typed methods (nextInt, nextDouble, ...)
                                  convenient but SLOWER than BufferedReader (regex + parsing overhead)
```

**Remember:**
1. `System.out`/`System.err`/`System.in` are static fields of `System`, of types `PrintStream`/`PrintStream`/`InputStream` respectively.
2. `InputStream`/`OutputStream` are byte-based and abstract; `Reader`/`Writer` are the character-based equivalent hierarchy.
3. Raw `System.in.read()` reads one byte at a time — extremely slow for real input.
4. `BufferedReader` needs `InputStreamReader` as a bridge because it works with characters, not bytes.
5. `Scanner` is far more convenient (typed methods, no manual parsing) but slower than `BufferedReader` due to tokenization and regex use internally.
6. Watch for the `nextInt()` + `nextLine()` leftover-newline bug — a classic, easy-to-hit mistake.
7. Prefer `Scanner` for everyday code; prefer `BufferedReader` when performance genuinely matters.


## 21. Immutable Classes, Shallow Copy, and Defensive Copying


### 0. Corrections to the video (read first)

| Video says | Accurate version |
|---|---|
| "Mark the class `final`" as a universal rule | `final` prevents **subclassing that overrides behaviour**, which is one real threat to immutability. But it is not strictly required by every immutability definition — some designs (Effective Java, Item 17) suggest making the **constructor `private`** with **static factory methods** instead, which achieves non-extensibility without forbidding subclassing outright. Either approach is acceptable; the goal is "no subclass can add mutation," not literally "the keyword `final` is mandatory." |
| "Primitives are stored on the stack, so they're inherently immutable" | More precise: a primitive **instance field** lives inside the object on the heap (not the stack — only **local variables** and method parameters live on the stack). What makes a `private final int` field immutable is the `final` keyword forbidding reassignment, not its storage location. The video's stack/heap distinction applies to local variables, not instance fields. |
| "Strings are immutable by default, so no special handling needed" | True, but the video doesn't explain *why*: `String` internally stores its characters in a `final` (and, since Java 9, often compact) array and never exposes a mutable reference to it — every operation that looks like modification (`concat`, `substring`, etc.) returns a **new** `String` object. This is exactly the defensive-copy/immutable-field pattern the video teaches, applied by the JDK itself. |
| "Shallow copy = what we discussed with `s1 = s2` earlier" | Precisely: `s1 = s2` (assigning one reference to another) is a **reference copy** — no new object at all, both variables point to the same object. **Shallow copy** specifically refers to creating **a new outer object** whose fields are copied by reference (so nested mutable objects are still shared) — a related but distinct concept, which the video's own resolution (comparing `s1.getCollege()` returning the same reference) correctly demonstrates. |
| Collections/arrays not mentioned | A field can leak mutability through more than one nested custom object — **mutable collections** (`List`, `Map`, `Set`) and **arrays** are extremely common real-world leaks in immutable classes and need the same defensive-copy treatment (see §6). |
| "Immutable objects are useful for threads, we'll see later" | Correctly deferred, but worth stating now: immutability is useful **beyond threading** too — as safe `HashMap`/`HashSet` keys (since `hashCode()` never changes), for safe caching, and for reasoning about code without tracking every place a shared object might be mutated. |



### 1. What is an immutable object?

An object is **immutable** if, once created, **none of its state can be changed** — not its fields, not its behaviour, through any code path whatsoever.

```java
Student s1 = new Student(28, "Aditya");   // create it once
// after this point, absolutely nothing about s1 can change
```

An immutable object is the opposite of the objects you've built so far, where a setter (or even just a public field) lets any caller freely rewrite the object's state after construction.



### 2. Why a normal class is mutable

```java
class Student {
    private int age;
    private String name;

    Student(int age, String name) { this.age = age; this.name = name; }

    public void setName(String name) { this.name = name; }   // setter: mutation point #1
    public void setAge(int age)      { this.age = age; }      // mutation point #2
}

class EngineeringStudent extends Student {
    @Override
    void markAttendance() { /* overridden behaviour */ }       // mutation point #3: subclassing
}
```

Three distinct ways an object's state or behaviour can be changed after construction:
1. **Setters** — direct field mutation via a public method.
2. **Subclassing and overriding** — a child class changes what a method *does*, even if the field values stay the same.
3. **Direct field access** — if a field isn't `private`, any code can reassign it.

To build an immutable class, each of these needs to be closed off.



### 3. The rules for an immutable class

| Rule | Purpose |
|---|---|
| **Mark the class `final`** (or make the constructor `private` + provide static factories) | Prevents subclasses from overriding methods to change behaviour |
| **Mark every field `private` and `final`** | `private` blocks direct outside access; `final` ensures each field is assigned exactly once (in the constructor) and never reassigned |
| **Provide no setters** | Removes the most obvious mutation path entirely |
| **Assign fields only through the constructor** | The only place state is ever set |
| **Provide only getters** (if any accessors are needed) | Read-only access to the state |

```java
final class Student {
    private final int age;
    private final String name;

    Student(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public int getAge()      { return age; }
    public String getName()  { return name; }
    // no setName(), no setAge()
}
```

```java
Student s1 = new Student(28, "Aditya");
s1.getName();          // OK — read-only
// s1.setName("Rohit"); // ERROR: no such method exists
// s1.name = "Rohit";   // ERROR: name is private
class CSStudent extends Student { }   // ERROR: cannot subclass a final class
```

At this point, with only primitive-typed fields (`int`) and `String` fields, this class **is genuinely immutable** — every rule above is sufficient. The trouble starts when a field's type is a **mutable, non-primitive, custom class**.



### 4. The trap: a nested mutable object breaks immutability

Suppose `Student` needs a more detailed `college` field — not just a `String`, but its own class:

```java
class College {                          // a plain, ordinary MUTABLE class
    String name;
    String address;

    College(String name, String address) {
        this.name = name;
        this.address = address;
    }
}
```

```java
final class Student {
    private final int age;
    private final String name;
    private final College college;        // a reference to a mutable object

    Student(int age, String name, College college) {
        this.age = age;
        this.name = name;
        this.college = college;             // stores the reference AS GIVEN
    }

    public int getAge()          { return age; }
    public String getName()      { return name; }
    public College getCollege()  { return college; }   // returns the reference AS IS
}
```

Every rule from §3 is still followed: the class is `final`, fields are `private final`, there are no setters. And yet:

```java
College c = new College("IIT Guwahati", "Assam");
Student s1 = new Student(28, "Aditya", c);

System.out.println(s1.getCollege().getName());   // IIT Guwahati

s1.getCollege().name = "IIT Bombay";              // mutates the SAME College object

System.out.println(s1.getCollege().getName());   // IIT Bombay  ← changed!
```

`s1`'s state has changed, even though nothing was ever assigned to `s1` directly and `Student` has no setters at all. **This class is not truly immutable.**



### 5. Why this happens: shallow copy (reference leakage)

```
STACK                       HEAP
┌───────────┐               ┌──────────────────────┐
│ c    ──────────────────►  │ College object         │
├───────────┤               │  name = "IIT Guwahati" │
│ s1   ──────────────────►  │  address = "Assam"     │
└───────────┘               └───────────▲────────────┘
                                          │
                    Student object       │
                    ┌────────────────────┴──┐
                    │  age = 28               │
                    │  name = "Aditya"        │
                    │  college  ──────────────┘   (same reference as c)
                    └─────────────────────────┘
```

- The `Student` constructor stores the **exact reference** it was given (`this.college = college;`), rather than creating its own copy — so `s1`'s `college` field and the caller's `c` variable point to the **very same `College` object**.
- `getCollege()` returns that **same reference** — so the caller can dereference it and mutate the shared object directly.
- Neither the constructor nor the getter ever created a new object; they both just **copied a pointer**. This is exactly what makes it a **shallow copy**: the outer object (`Student`) is distinct, but its nested mutable fields are shared with the outside world, not copied.

**Note the distinction from a pure reference copy** (`s1 = s2`, discussed in earlier notes): here, `Student` objects `s1` and any other instance are genuinely separate objects with separate memory — the leak is narrower and subtler, confined to the shared `college` reference within them.

#### Beyond the video

This exact bug has a name in real-world code: **representation exposure** (Effective Java, Item 50) — a class "leaking" a mutable internal field to the outside world, whether via a constructor parameter stored directly or via a getter returning the live reference.



### 6. The fix: defensive copying (deep copy)

**Defensive copy:** whenever a mutable object crosses the boundary of your class — coming **in** via the constructor, or going **out** via a getter — create a **brand-new copy** of it instead of sharing the original reference.

```java
final class Student {
    private final int age;
    private final String name;
    private final College college;

    Student(int age, String name, College college) {
        this.age = age;
        this.name = name;
        this.college = new College(college.name, college.address);   // defensive copy IN
    }

    public int getAge()         { return age; }
    public String getName()     { return name; }
    public College getCollege() {
        return new College(this.college.name, this.college.address);  // defensive copy OUT
    }
}
```

Two defensive copies are needed — **both matter independently**:

| Copy point | Protects against |
|---|---|
| **Constructor** (copy on the way **in**) | The caller mutating the original object *after* passing it in (`c.name = "..."` after construction would otherwise still affect `s1`) |
| **Getter** (copy on the way **out**) | The caller mutating the object obtained *from* the getter (the exact bug demonstrated in §4) |

```java
College c = new College("IIT Guwahati", "Assam");
Student s1 = new Student(28, "Aditya", c);

s1.getCollege().name = "IIT Bombay";     // mutates only the COPY returned by getCollege()
System.out.println(s1.getCollege().getName());  // still "IIT Guwahati" — unaffected
```

#### Memory picture after defensive copying

```
STACK                    HEAP
┌────────┐                ┌──────────────────────┐
│ c  ───────────────────►  │ College (original)     │
├────────┤                │  name = "IIT Guwahati" │
│ s1 ───────────────────►  │  address = "Assam"     │
└────────┘                └──────────────────────┘
                          ┌──────────────────────┐
       Student object ───►│ College (own copy)      │  ← distinct object, made in constructor
       college field      │  name = "IIT Guwahati"  │
                          │  address = "Assam"      │
                          └──────────────────────┘
       (each call to getCollege() creates YET ANOTHER new College copy, returned to the caller)
```

`Student` never hands out a reference to its own internal `College` object — every external interaction gets a **fresh, independent copy**. Nobody outside can ever reach (and therefore never mutate) the `College` object that `Student` actually holds.

**This — creating a genuinely separate copy of a nested object, rather than sharing a reference — is what "deep copy" means.**



### 7. Two ways to make a class truly immutable

| Approach | When to use |
|---|---|
| **Make the nested class itself immutable** (apply all the rules from §3 to `College` too: `final` class, `private final` fields, no setters, only getters) | When you control/own the nested class |
| **Defensive copying** in the constructor and getters of the outer class | When you **don't** control the nested class (e.g., it's from an external library, or must stay mutable for other reasons) |

If `College` itself follows every immutability rule, `Student` doesn't need defensive copies at all — a reference to an object that can never change is safe to share freely. This is why primitives (`int`, `double`, etc.) never need special handling: they're stored by value, not by reference, so there's nothing to leak. And why `String` fields never need it either — `String` is immutable by design in the JDK.



### 8. Beyond the video: additional immutability leaks to guard

**Collections and arrays** are common real-world sources of the exact same bug, since they're mutable by default:

```java
final class Team {
    private final List<String> members;

    Team(List<String> members) {
        this.members = new ArrayList<>(members);       // defensive copy IN
    }

    public List<String> getMembers() {
        return List.copyOf(members);                    // defensive copy OUT (unmodifiable + new)
        // or: return Collections.unmodifiableList(new ArrayList<>(members));
    }
}
```

Returning the live `List` reference directly (`return members;`) lets a caller call `.add()`/`.remove()` on it and silently mutate the "immutable" object — the exact same class of bug as the `College` example, just with a built-in collection instead of a custom class.

**Arrays** are especially dangerous because there is no immutable array type in Java at all — an array field must **always** be defensively copied (`Arrays.copyOf(...)`) on both entry and exit if true immutability is required.

**Records (Java 16+)** simplify a lot of this for simple data carriers — `record Student(int age, String name) {}` auto-generates a `final` class with `private final` fields, a canonical constructor, and accessors. But records **do not automatically defensively copy** mutable component fields — if a record has a mutable field (a `List`, an array, a mutable custom object), the same defensive-copying rules from this document still apply; you write it explicitly inside a compact constructor and inside custom accessor overrides.

```java
record Team(List<String> members) {
    Team {                                              // compact constructor
        members = new ArrayList<>(members);              // still needs a defensive copy
    }
    public List<String> members() {                      // override the auto-generated accessor
        return List.copyOf(members);
    }
}
```



### 9. Full example: everything together

```java
final class College {
    private final String name;
    private final String address;

    College(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName()    { return name; }
    public String getAddress() { return address; }
}

final class Student {
    private final int age;
    private final String name;
    private final College college;

    Student(int age, String name, College college) {
        this.age = age;
        this.name = name;
        this.college = college;    // safe now — College is itself immutable, nothing to leak
    }

    public int getAge()         { return age; }
    public String getName()     { return name; }
    public College getCollege() { return college; }   // safe to share directly
}
```

Making `College` immutable in its own right (approach #1 from §7) is simpler and equally correct — no defensive copying needed anywhere, because there's nothing mutable left to protect against.



### 10. Why immutability matters (a preview)

- **Thread safety**: in concurrent code, multiple threads reading a shared object can cause race conditions if any thread can mutate it mid-read. An immutable object can never be caught "half-changed," so it needs no locking to share safely across threads.
- **Safe hash keys**: mutable objects used as `HashMap`/`HashSet` keys can become unfindable if their `hashCode()` changes after insertion — immutable keys never have this problem.
- **Safe caching and sharing**: an immutable object can be freely cached, shared, or reused (e.g., the `Integer` cache from earlier notes) without ever needing a defensive copy at the point of sharing — because there is nothing to protect against.
- **Simpler reasoning**: code that operates on immutable data doesn't require tracking every place an object might later be changed — its state is fixed for its entire lifetime.



### 11. Interview questions

| Question | Answer |
|---|---|
| What is an immutable object? | An object whose state (fields) and behaviour cannot be changed after construction, through any code path. |
| What are the standard rules for building an immutable class? | Make the class `final` (or private constructor + static factories), make all fields `private final`, provide no setters, assign fields only in the constructor, expose only getters. |
| Is a class with only primitive and `String` fields, following those rules, automatically immutable? | Yes — primitives are copied by value, and `String` is immutable by design, so there's nothing left to leak. |
| Why does a class with a mutable non-primitive field break immutability, even with all the standard rules applied? | Because storing/returning the reference directly lets external code reach into the shared nested object and mutate its fields, bypassing the outer class entirely. |
| What is a shallow copy? | Copying an object such that its non-primitive fields are copied by reference — the copy and the original still share the same nested mutable objects. |
| What is a deep copy? | Copying an object such that every mutable nested object is also independently copied — no shared references to mutable state anywhere. |
| What is defensive copying? | Creating a new copy of a mutable object whenever it crosses a class boundary — on the way in (constructor) and on the way out (getter) — instead of sharing the original reference. |
| Why is defensive copying needed at BOTH the constructor and the getter? | The constructor copy protects against the caller mutating the original after passing it in; the getter copy protects against the caller mutating the object obtained from the getter. Either one alone leaves a leak. |
| What's the alternative to defensive copying? | Make the nested class itself immutable — then there's no mutable state to protect and no copying is needed at all. |
| Do arrays and collections have the same immutability leak risk as custom objects? | Yes — a `List`/`Map`/array field returned or stored by reference lets outside code mutate it directly; needs the same defensive-copy (or unmodifiable-wrapper) treatment. |
| Do Java records automatically prevent this kind of leak? | No — records generate a canonical constructor and accessors, but don't defensively copy mutable component fields automatically; you still add that logic explicitly (compact constructor, custom accessor). |
| Why are immutable objects useful in multithreaded code? | They can never be caught in a partially-mutated state by another thread, so they can be shared across threads without synchronization. |
| Why is `String` immutable in Java? | Its internal character storage is `final` and never exposed mutably; every apparent modification method returns a new `String` object instead of changing the existing one. |



### 12. Summary / mental model

```
IMMUTABLE CLASS CHECKLIST
  ✓ class final (or private constructor + static factories)
  ✓ all fields private final
  ✓ no setters
  ✓ fields assigned only in the constructor
  ✓ only getters exposed

BUT: a mutable non-primitive field still leaks state unless you either

  (A) make that nested class immutable too — nothing left to protect
  OR
  (B) defensively copy it:
        constructor:  this.field = new Nested(given.x, given.y);   ← copy IN
        getter:       return new Nested(this.field.x, this.field.y); ← copy OUT

SHALLOW COPY  → new outer object, but nested mutable fields SHARED (reference copied)
DEEP COPY     → new outer object AND new copies of every nested mutable object

Primitives and Strings never need defensive copying — nothing to leak (by value / immutable by design).
Collections and arrays need it too — same leak, same fix.
```

**Remember:**
1. `final` class + `private final` fields + no setters is necessary but **not sufficient** once any field is a mutable reference type.
2. Returning or storing a mutable reference directly is a shallow copy — the "immutable" object can still be mutated through it.
3. Defensive copying at both the constructor and every getter closes the leak (deep copy).
4. The simplest fix is often to make the nested type immutable in the first place — no copying needed anywhere.
5. This applies equally to custom classes, collections, and arrays.
6. Immutability's payoff: thread safety without locks, safe hash keys, safe caching, and simpler reasoning about state.


## 22. The Object Class: toString, equals, hashCode, getClass, clone, finalize


### 0. Corrections to the video (read first)

| Video says | Accurate version |
|---|---|
| "hashCode returns a hexadecimal int" | `hashCode()` returns a plain **`int`** (a 32-bit signed integer, base 10 internally). What the video sees printed after `toString()`'s `@` is the hash code **formatted as a hexadecimal string** by `toString()` itself (`Integer.toHexString(hashCode())`) — the hash code's actual type and value are decimal `int`; only its *display* in `toString()`'s default output is hex. |
| Default `hashCode()` "generates a random number, might collide" | More precisely: the default (`Object`'s) implementation is typically derived from the object's **memory address or an internal identity token** at the time of creation (JVM-specific, not "random" in the sense of `Math.random()`) — collisions are possible but the value is deterministic per object instance for its lifetime, not re-randomized on each call. |
| "clone() throws CloneNotSupportedException, always write `throws` and implement Cloneable" | Correct mechanically, but **Beyond the video**: `clone()` is widely considered a **flawed API** in modern Java (Effective Java, Item 13 recommends avoiding it) — favor a **copy constructor** or a **static copy factory method** instead, which don't require checked exceptions, marker interfaces, or the shallow/deep copy ambiguity `clone()` carries. |
| "finalize() is deprecated" | Correct, and worth being precise: `finalize()` was **deprecated in Java 9** and its use is **strongly discouraged**; it was **marked for removal in Java 18 (JEP 421)** — modern code should use **try-with-resources** and `AutoCloseable`, or `java.lang.ref.Cleaner` (introduced in Java 9) as the supported replacement for cleanup logic. |
| "Arrays have Object above them, primitives don't" | Correct — but **Beyond the video**: this is precisely because an array in Java is itself a genuine **object** (with a `.length` field and inherited `Object` methods like `.getClass()`, `.clone()`), even though its element type may be primitive (`int[]` holds primitive `int`s, but the array itself is a reference type on the heap). |



### 1. Why does `Object` exist? Every class's root parent

`Object` (in `java.lang.Object`) is the **implicit superclass of every class in Java** — whether you write it explicitly or not, and whether directly or through a chain of inheritance.

```java
class Student { }                        // compiler treats this as:
class Student extends Object { }         // ...identical, automatically
```

Since Java forbids **multiple inheritance of classes**, even a class with an explicit superclass eventually traces back to `Object` at the top of its hierarchy:

```java
class Human { }                          // implicitly extends Object
class Student extends Human { }          // Student → Human → Object
```

Whatever the chain, there's always exactly one root: `Object`.

#### Why Java needs a single root class

**1. Common behaviour for every class.** Certain capabilities (converting to a readable string, comparing for equality, getting a unique-ish identity number, finding out the runtime type) are useful for *every* object, no matter what it represents. Placing these as methods on `Object` means **every class in Java automatically inherits them**, with no extra work.

```java
class Student { }
Student s1 = new Student();
s1.toString();     // works immediately — inherited from Object, no need to write it yourself
```

**2. A universal reference type, enabling polymorphism.** Because `Object` sits at the root, a variable of type `Object` can point to **any** object of any class:

```java
Object obj = new Student();       // legal: Object is the parent of every class, directly or indirectly
```

This is the same upcasting principle from inheritance (a parent reference can point to a child object) taken to its logical extreme — one type that can hold literally anything. On such a reference, only the methods declared on `Object` itself are directly callable (without a cast) — but that's still guaranteed to include `toString()`, `equals()`, `hashCode()`, and `getClass()` for absolutely anything.



### 2. The methods on `Object`

| Category | Methods |
|---|---|
| **Core methods** | `toString()`, `equals(Object)`, `hashCode()`, `getClass()` |
| **Cloning** | `clone()` |
| **Garbage collection (deprecated)** | `finalize()` |
| **Threading** (covered separately, under multithreading) | `wait()`, `notify()`, `notifyAll()` |



### 3. `toString()`

```java
public String toString() { ... }     // default implementation, defined in Object
```

**Purpose:** returns a **string representation of any object**.

#### Default behaviour

```java
class Student {
    String name;
    int age;
}

Student s1 = new Student();
System.out.println(s1.toString());   // Student@1b6d3586   (getClass().getName() + "@" + Integer.toHexString(hashCode()))
```

The default format is: **fully qualified class name** + `@` + the object's **hash code, formatted as hexadecimal**. This is consistent for the same object across repeated calls (its identity doesn't change), but carries no useful information about the object's actual state.

#### Overriding it

```java
class Student {
    String name;
    int age;

    @Override
    public String toString() {
        return name + ", " + age;
    }
}

Student s1 = new Student();
s1.name = "Aditya"; s1.age = 28;
System.out.println(s1.toString());   // Aditya, 28
```

#### Beyond the video: `println` calls `toString()` implicitly

```java
System.out.println(s1);              // identical output to s1.toString() — no need to write .toString() explicitly
```

`println(Object)` internally calls `String.valueOf(obj)`, which calls `obj.toString()` (with a `null` check). This is why printing any object directly, without explicitly calling `.toString()`, already uses your override.

#### Beyond the video: use in string concatenation and debugging

```java
String message = "Student: " + s1;   // implicitly calls s1.toString()
```

Overriding `toString()` is standard practice for almost every domain class — it makes debugging, logging, and console output vastly more useful than the default hex-address format. IDEs, debuggers, and logging frameworks all rely on it.



### 4. `equals(Object)`

```java
public boolean equals(Object obj) { ... }
```

**Purpose:** compares **two objects** and returns whether they should be considered equal.

#### Why the parameter type is `Object`, not the calling class's own type

`equals()` is declared inside `Object`, which has no idea what subclasses will exist. Since `Object` is the universal parent, accepting an `Object` parameter guarantees `equals()` can be called with **any** object as the argument, from any class — total generality, at the cost of needing a cast inside the method body when overriding it (see below).

#### Default behaviour: reference comparison

```java
class Student { String name; int age; }

Student s1 = new Student(); s1.name = "Aditya"; s1.age = 28;
Student s2 = new Student(); s2.name = "Aditya"; s2.age = 28;

System.out.println(s1.equals(s2));   // false — same field values, but DIFFERENT objects (different references)
```

The default implementation is exactly:
```java
public boolean equals(Object obj) {
    return (this == obj);       // reference comparison, identical to the == operator
}
```

So, before overriding, `.equals()` and `==` behave identically for any custom object — both simply check whether two references point to the exact same object in memory.

#### Overriding it (value-based equality)

```java
@Override
public boolean equals(Object obj) {
    if (obj == null)                          return false;                 // step 1: null check
    if (this == obj)                          return true;                  // step 2: same-reference shortcut
    if (this.getClass() != obj.getClass())    return false;                 // step 3: same runtime type check
    Student s = (Student) obj;                                                // step 4: safe cast
    return this.name.equals(s.name) && this.age == s.age;                    // step 5: field-by-field comparison
}
```

Each guard matters:

| Guard | Protects against |
|---|---|
| `obj == null` check | `NullPointerException` when later dereferencing `obj`'s fields |
| `this == obj` shortcut | Unnecessary work when comparing an object to itself |
| `getClass()` comparison | `ClassCastException` from casting an incompatible type (e.g., passing an `Integer` where a `Student` was expected) |
| Cast, then compare fields | The actual value-based equality logic |

```java
Student s1 = new Student(); s1.name = "Aditya"; s1.age = 28;
Student s2 = new Student(); s2.name = "Aditya"; s2.age = 28;
System.out.println(s1.equals(s2));    // true — now compares field values, not references
```

#### Beyond the video: `getClass()` vs `instanceof` in `equals()`

The video uses `getClass()`, which requires the **exact same runtime class** on both sides — a subclass instance is never equal to a parent instance, even if all inherited fields match. Using `instanceof` instead is more permissive (it also accepts subclasses) but can break the **symmetry** requirement of the `equals()` contract (`a.equals(b)` must equal `b.equals(a)`) in inheritance hierarchies. The `getClass()` approach, as the video demonstrates, is the safer default for most cases; the `instanceof` approach is preferred specifically when subclasses are meant to compare equal to their parent type (with careful symmetry handling).

#### Beyond the video: `Objects.equals()` for null-safety

```java
import java.util.Objects;

@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Student s)) return false;   // pattern-matching instanceof (Java 16+)
    return Objects.equals(name, s.name) && age == s.age;
}
```

`Objects.equals(a, b)` handles `null` safely on either side (`a == null && b == null` → `true`; one `null` → `false`), avoiding a manual null check on each field.



### 5. `hashCode()`

```java
public int hashCode() { ... }
```

**Purpose:** returns an **integer representation of an object**, intended to act as a fast, approximate identity/bucket value — heavily used internally by hash-based collections (`HashMap`, `HashSet`, `Hashtable`).

#### Default behaviour

The default is typically derived from the object's internal identity (JVM-specific — commonly related to its memory address at creation), and stays constant across calls for a given object instance, but the video's textual display shows it in hexadecimal (via `toString()`'s formatting) even though the returned value itself is a decimal `int`.



### 6. The equals-hashCode contract (critical rule)

> **If two objects are equal (via `.equals()`), their hash codes MUST be equal.**
> **The reverse is NOT guaranteed** — two unequal objects *may* coincidentally share the same hash code.

```java
Student s1 = new Student(); s1.name = "Aditya"; s1.age = 28;
Student s2 = new Student(); s2.name = "Aditya"; s2.age = 28;

s1.equals(s2);          // true (after overriding equals())
s1.hashCode() == s2.hashCode();   // MUST also be true — otherwise the contract is broken
```

```java
Student s3 = new Student(); s3.name = "Rohit"; s3.age = 28;
// s1.equals(s3) is false — different names
// but s1.hashCode() == s3.hashCode() COULD coincidentally be true — that's allowed (not required, not forbidden)
```

#### Why the contract exists

If `equals()` is overridden but `hashCode()` is **not**, the contract silently breaks: two objects that are `.equals()`-equal can end up with **different** hash codes (since the unoverridden default is still reference-based).

**Consequence:** hash-based collections (`HashMap`, `HashSet`) rely on this contract internally to decide which "bucket" an object belongs in. If two equal objects hash differently, the collection can't reliably find one using the other as a lookup key — effectively breaking membership checks, duplicate detection, and retrieval. **Always override `equals()` and `hashCode()` together, never just one.**

#### Overriding `hashCode()` — manual approach (educational)

```java
@Override
public int hashCode() {
    int result = 17;                          // start with a non-zero prime
    result = result * 31 + age;               // multiply by another prime, factor in each field
    result = result * 31 + (name == null ? 0 : name.hashCode());
    return result;
}
```

- Starting from a nonzero prime and repeatedly multiplying by another prime while folding in each field's own hash is the classic algorithm (this is essentially what IDEs and Lombok generate).
- Primes reduce the likelihood of collisions when combining multiple field values.
- `name.hashCode()` reuses `String`'s own overridden `hashCode()` rather than reinventing character-based hashing.
- **Always null-check** before calling `.hashCode()` on a field, or you'll get a `NullPointerException`.

#### The modern, standard approach

```java
import java.util.Objects;

@Override
public int hashCode() {
    return Objects.hash(name, age);      // handles nulls internally, combines fields correctly
}
```

`Objects.hash(...)` (from `java.util.Objects`) is the idiomatic modern replacement for manually writing the prime-multiplication logic — it's null-safe and combines any number of fields correctly.



### 7. `getClass()`

```java
public final Class<?> getClass() { ... }
```

**Purpose:** returns the **runtime class** of an object — which concrete class was actually instantiated, regardless of the reference type used to refer to it.

```java
Student s1 = new Student();
System.out.println(s1.getClass().getName());   // "Student"
```

- `getClass()` returns an object of type **`Class<?>`** — yes, Java has a class literally named `Class`, used for **reflection** (a more advanced topic, covered separately).
- `getClass()` is declared **`final`** — it **cannot be overridden**, because it must always report the true runtime type, which no subclass should be allowed to fake.

```java
class Animal { }
class Dog extends Animal { }

Animal a = new Animal();
Animal d = new Dog();          // reference type Animal, but actual object is a Dog

System.out.println(a.getClass().getName());   // Animal
System.out.println(d.getClass().getName());   // Dog — reports the ACTUAL object's class, not the reference type
```



### 8. `instanceof`

```java
d instanceof Animal    // true or false
```

**Purpose:** checks whether an object is an instance of a given class **or any of its subclasses**.

```java
Animal a = new Animal();
Animal d = new Dog();

System.out.println(a instanceof Animal);   // true
System.out.println(d instanceof Animal);   // true  — Dog IS-A Animal, so this holds
System.out.println(a instanceof Dog);      // false — Animal is NOT a Dog (no such relationship exists)
```

#### `getClass()` vs `instanceof`

| | `getClass()` | `instanceof` |
|---|---|---|
| Tells you | The exact runtime class | Whether the object is that type **or a subtype** |
| Comparison style | `a.getClass() == b.getClass()` — exact match only | `a instanceof SomeType` — matches the type or any subclass |
| Typical use | Strict equality checks (as in `equals()`) | General type checks, safe casting guards |

```java
if (obj instanceof Student s) {    // pattern-matching instanceof (Java 16+): checks type AND casts in one step
    System.out.println(s.name);     // s is already safely cast here
}
```



### 9. `clone()`

```java
protected Object clone() throws CloneNotSupportedException { ... }
```

**Purpose:** creates a **copy of an object**.

#### Key facts about its signature

| Trait | Detail |
|---|---|
| Access modifier | `protected` (not `public`) — can only be called from within the class itself, a subclass, or the same package by default |
| Return type | `Object` (requires a cast back to the actual type after calling) |
| Throws | Checked exception `CloneNotSupportedException` |

#### Why you must implement `Cloneable` to use it

```java
class Student implements Cloneable {         // required, or clone() throws at runtime
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();                 // delegates to Object's default clone logic
    }
}

Student s1 = new Student();
Student s3 = (Student) s1.clone();            // cast required, since clone() returns Object
```

Without `implements Cloneable`, calling `.clone()` (even after overriding it) throws `CloneNotSupportedException` at runtime. The rule: **"not every object should be cloneable"** — some objects (database connections, thread handles, anything wrapping an external resource) would produce broken or undesired behaviour if blindly duplicated, so Java requires an explicit opt-in.

#### `Cloneable`: a marker interface

```java
public interface Cloneable { }     // completely empty — no methods at all
```

`Cloneable` has **zero methods**. Implementing an empty interface doesn't require overriding anything (there's nothing to override) — it exists purely as a **marker**: a flag that says "this class explicitly opts in to cloning." Internally, `Object.clone()`'s default logic checks whether the calling object's class implements `Cloneable`; if not, it throws `CloneNotSupportedException`.

```java
// Conceptual internal logic of Object.clone()
if (this instanceof Cloneable) {
    // proceed with copying
} else {
    throw new CloneNotSupportedException();
}
```

Other examples of Java's marker interfaces: `Serializable`, `Remote`.

#### The default clone is a shallow copy

```java
class College { String name; }
class Student implements Cloneable {
    String name;
    College college;
    @Override protected Object clone() throws CloneNotSupportedException { return super.clone(); }
}
```

`super.clone()`'s default behaviour copies each field **by value for primitives, and by reference for objects** — so a cloned `Student`'s `college` field points to the **same** `College` object as the original. This is a shallow copy, exactly as covered in the immutability notes. To get a deep copy, you must override `clone()` to manually clone nested mutable fields too:

```java
@Override
protected Object clone() throws CloneNotSupportedException {
    Student cloned = (Student) super.clone();
    cloned.college = new College(this.college.name);   // manually deep-copy the nested object
    return cloned;
}
```

#### Beyond the video: why `clone()` is considered a flawed design (Effective Java, Item 13)

Modern guidance generally **avoids `clone()` entirely**, preferring:

```java
// Copy constructor
Student(Student other) {
    this.name = other.name;
    this.college = new College(other.college.name);   // deep-copy explicitly, clearly, no exception needed
}

// or a static copy factory
static Student copyOf(Student other) {
    return new Student(other.name, new College(other.college.name));
}
```

Reasons `clone()` is disfavored: it forces a checked exception even when cloning can never actually fail for a well-formed class; the shallow-vs-deep-copy behaviour is easy to get wrong silently; the cast from `Object` is clunky; and it interacts awkwardly with `final` fields (which `clone()`'s field-by-field copy mechanism bypasses constructors for). Copy constructors and static factories avoid every one of these problems with plain, explicit code.



### 10. `finalize()` — deprecated

```java
protected void finalize() throws Throwable { ... }
```

**Historical purpose:** called by the garbage collector before reclaiming an object's memory, intended as a last chance to release resources.

**Why it's deprecated:**
- **Unpredictable** — there's no guarantee *when* (or even *if*) it will run; it depends entirely on the garbage collector's decisions.
- **Unsafe** — an object can be "resurrected" (a reference to `this` escaping during `finalize()`) in ways that create fragile edge cases.
- **Unreliable** — calling `System.gc()` yourself doesn't guarantee `finalize()` runs either.

**Modern replacement:** `finalize()` was **deprecated in Java 9** and marked for removal in later versions. Use:
- **`try-with-resources`** + implementing `AutoCloseable`, for deterministic, immediate cleanup, or
- **`java.lang.ref.Cleaner`** (introduced in Java 9), for cleanup tied to garbage collection but implemented far more safely than `finalize()`.

```java
class Resource implements AutoCloseable {
    @Override public void close() { System.out.println("Resource released"); }
}

try (Resource r = new Resource()) {
    // use r
}   // close() is called automatically and deterministically here, unlike finalize()
```



### 11. Arrays and `Object`

Arrays in Java are **non-primitive** (reference) types, even when they hold primitive elements — created with `new`, allocated on the heap, and referenced by a variable holding an address:

```java
int[] arr = new int[5];      // arr: reference variable (stack); the 5-element array itself: heap object
```

Because arrays are objects, `Object`'s hierarchy sits above them too — arrays inherit `Object`'s methods (`getClass()`, `clone()`, `equals()`, `hashCode()`, `toString()`), though several (like `equals()`) retain their default reference-comparison behaviour rather than anything array-content-aware.

```java
int[] arr = new int[5];
System.out.println(arr.getClass().getName());   // [I  (JVM's internal encoding for "array of int")
```

#### Beyond the video: `array.clone()` works without `Cloneable`

Unusually, **arrays support `.clone()` without needing to implement `Cloneable` explicitly** — the JVM treats array cloning as a special built-in case. `int[] copy = arr.clone();` works directly and performs a shallow copy (fine for primitive arrays, since there's nothing to share by reference; for object arrays, it copies references, not the referenced objects).



### 12. Primitives do NOT sit under `Object`

```java
int a = 5, b = 5;
a == b;              // comparison — fine
// a.equals(b)       // ERROR: int is a primitive, has no methods at all
```

Primitives (`int`, `char`, `float`, `boolean`, etc.) are **not stored as objects** — no memory header, no method table, nothing `Object`-related sits above them. That's precisely why you can't call `.toString()`, `.equals()`, or any other method directly on a primitive value.

Their **wrapper classes** (`Integer`, `Character`, `Float`, `Boolean`, etc.), being ordinary non-primitive classes, **do** sit under `Object` like everything else — which is exactly why `Integer.equals()`, `Integer.hashCode()`, and `Integer.toString()` all exist and work as expected.



### 13. Full example: everything together

```java
import java.util.Objects;

class College {
    String name;
    College(String name) { this.name = name; }
}

class Student implements Cloneable {
    private String name;
    private int age;
    private College college;

    Student(String name, int age, College college) {
        this.name = name; this.age = age; this.college = college;
    }

    @Override
    public String toString() {
        return name + ", " + age + ", " + college.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student s = (Student) obj;
        return age == s.age && Objects.equals(name, s.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Student cloned = (Student) super.clone();
        cloned.college = new College(this.college.name);   // deep-copy the nested mutable field
        return cloned;
    }
}

public class Demo {
    public static void main(String[] args) throws CloneNotSupportedException {
        College c = new College("IIT Guwahati");
        Student s1 = new Student("Aditya", 28, c);
        Student s2 = new Student("Aditya", 28, c);

        System.out.println(s1);                          // Aditya, 28, IIT Guwahati  (toString via println)
        System.out.println(s1.equals(s2));                // true (value-based)
        System.out.println(s1.hashCode() == s2.hashCode()); // true (contract respected)
        System.out.println(s1.getClass().getName());       // Student
        System.out.println(s1 instanceof Student);          // true

        Student s3 = (Student) s1.clone();
        System.out.println(s3);                            // Aditya, 28, IIT Guwahati
        s3.college.name = "IIT Bombay";
        System.out.println(s1.college.name);                // still "IIT Guwahati" — deep-copied, unaffected
    }
}
```



### 14. Interview questions

| Question | Answer |
|---|---|
| What is the root class of every class in Java? | `java.lang.Object`, either directly or through the inheritance chain. |
| Why does Java have a single root class? | To provide common behaviour (`toString`, `equals`, `hashCode`, `getClass`) to every class automatically, and to enable an `Object` reference to point to any object (a foundation for generic/polymorphic code). |
| What does `toString()` return by default? | The class's fully qualified name, `@`, and the object's hash code in hexadecimal. |
| Why is `println(obj)` equivalent to `println(obj.toString())`? | `println(Object)` calls `String.valueOf(obj)`, which internally calls `obj.toString()`. |
| What does `equals()` compare by default? | References — identical to `==` — until overridden. |
| Why does `equals(Object obj)` take an `Object` parameter instead of the class's own type? | Because it's declared in `Object`, which has no knowledge of future subclasses; accepting `Object` allows the method to be called with any type. |
| What is the equals-hashCode contract? | If two objects are equal via `.equals()`, they must return the same `hashCode()`. The converse is not required — unequal objects may still share a hash code. |
| What happens if you override `equals()` but not `hashCode()`? | The contract breaks: equal objects can end up with different hash codes, causing hash-based collections (`HashMap`, `HashSet`) to behave incorrectly. |
| What does `getClass()` return, and can it be overridden? | The exact runtime class as a `Class<?>` object; it's `final` and cannot be overridden. |
| Difference between `getClass()` equality and `instanceof`? | `getClass()` matches only the exact same class; `instanceof` also matches subclasses. |
| What does `clone()` do by default? | A shallow copy — primitive fields copied by value, object fields copied by reference (shared, not duplicated). |
| Why must a class implement `Cloneable` to use `clone()`? | Not every object should be cloneable (e.g., resources, connections); `Cloneable` is an explicit opt-in marker, and its absence causes `clone()` to throw `CloneNotSupportedException`. |
| What is a marker interface? | An interface with no methods, used only to "tag" a class and enable some behaviour (e.g., `Cloneable`, `Serializable`). |
| Why is `clone()` discouraged in modern Java? | It forces a checked exception, defaults to shallow copying (easy to get wrong), requires an awkward cast, and interacts poorly with `final` fields. Prefer a copy constructor or static factory method. |
| Why is `finalize()` deprecated? | It's unpredictable (no guarantee it runs, or when), unsafe (objects can "resurrect" themselves), and unreliable. Deprecated in Java 9; replaced by `try-with-resources`/`AutoCloseable` or `java.lang.ref.Cleaner`. |
| Do arrays sit under `Object`? | Yes — arrays are non-primitive reference types, so they inherit `Object`'s methods, and can even use `.clone()` without implementing `Cloneable` (a special JVM case). |
| Do primitives sit under `Object`? | No — primitives have no methods at all. Their wrapper classes (`Integer`, `Character`, etc.) do sit under `Object`. |



### 15. Summary / mental model

```
Object                                     ← root of every class, directly or indirectly
 ├── toString()   → string representation (default: ClassName@hexHashCode)
 ├── equals(Object) → equality check (default: reference comparison, same as ==)
 ├── hashCode()    → integer identity value, used by hash-based collections
 ├── getClass()    → FINAL; exact runtime type, as a Class<?> object
 ├── clone()       → PROTECTED; shallow copy by default; needs `implements Cloneable`
 ├── finalize()    → DEPRECATED; use try-with-resources/AutoCloseable or Cleaner instead
 └── wait()/notify()/notifyAll() → threading (covered separately)

instanceof          → checks TYPE OR SUBTYPE   (separate from Object's own methods, but related)

EQUALS-HASHCODE CONTRACT:
  equals() true  ⟹  hashCode() must match
  hashCode() match  ⇏  equals() need NOT be true (collisions allowed)
  → Always override both together, never just one.

Arrays  → non-primitive → sit under Object (inherit its methods; .clone() works without Cloneable)
Primitives → NOT objects → nothing from Object applies; their WRAPPER CLASSES do sit under Object
```

**Remember:**
1. Every class in Java inherits from `Object`, giving it `toString()`, `equals()`, `hashCode()`, and `getClass()` for free.
2. Override `toString()` for readable output; override `equals()`/`hashCode()` together, never separately.
3. `getClass()` is exact-type and cannot be overridden; `instanceof` also matches subclasses.
4. `clone()` needs `Cloneable` (a marker interface) and defaults to a shallow copy — but modern Java favors copy constructors/static factories instead.
5. `finalize()` is deprecated and unreliable — use `try-with-resources`/`AutoCloseable` or `Cleaner`.
6. Arrays are objects (they sit under `Object`); primitives are not.



### Classes & Objects

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
|-|-|
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



#### Golden Rules — Constructors (full recap)

- ✅ Unassigned fields get automatic defaults: `0` for numbers, `false` for `boolean`, `null` for any object type — but **local variables never get defaults**, they must be explicitly assigned before use.
- ✅ Writing even one constructor removes Java's free default no-arg constructor — if you still need `new ClassName()` with no args, you must write it yourself.
- ✅ A constructor has **zero return type** — not even `void`. Adding a return type turns it into a regular method with the same name, not a constructor.
- ✅ Constructors can only run via `new`, or via `this(...)`/`super(...)` from within another constructor — never as a regular method call on an existing object.
- ✅ `this(...)` chaining avoids duplicating field-assignment logic across multiple constructors — one constructor does the real work, others just delegate with different defaults.
- ✅ `this(...)` must always be the **first line** in a constructor — nothing can execute before it.

Try Q1–Q8 (the coding ones) now with all this in mind — the chaining questions (7, 8) will make a lot more sense with Q13/Q14 fresh.
