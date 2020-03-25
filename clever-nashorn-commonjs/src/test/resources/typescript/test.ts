import { addNumber } from './lib';

const addNumberWrapper = (a: number, b: number): number => {
  console.log("addNumber --> 之前 a=", a, " | b=", b);
  const res = addNumber(a, b);
  console.log("addNumber --> 之后 res=", res);
  // console.log("### ------------------------------------> OK");
  return res;
}

export {
  addNumberWrapper,
}