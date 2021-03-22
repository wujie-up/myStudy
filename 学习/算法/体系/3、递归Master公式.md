1、Master公式适用于，递归子规模一致的情况，用于推算递归的复杂度

**T（N） =  a  *  T（N / b） + O（N ^ d）**

- **log(b ^ a) < d    O（N ^ d）**
- **log(b ^ a) > d    O（N ^ log(b ^ a)）**
- **log(b ^ a) == d    O（N ^ d * logN）**

