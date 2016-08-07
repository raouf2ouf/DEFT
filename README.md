# DEFT
DEFT for Defeasible Datalog+/- is an open source software for Defeasible Reasonning based on 
[Graal](https://graphik-team.github.io/graal). It is still under active developpment and offers a working API for defeasible reasonning in Datalog+/-.

For more details and examples [See DEFT homepage](https://hamhec.github.io/DEFT).


## Installation
Two ways to do it:

1. Easy way: Go to [target](https://github.com/hamhec/DEFT/tree/master/target) and download `DEFT-{version}-jar-with-dependencies.jar'. It contains all the dependencies including Graal.

2. Or, Flexible way:

* install graal as a maven dependency: [graal installation](https://github.com/graphik-team/graal).
* Clone the DEFT repository
~~~
https://github.com/hamhec/DEFT.git
~~~
* go to the DEFT folder and package it
~~~
mvn clean install
~~~
* add DEFT as a maven dependency to your project.
* profit :).

## Usage

1. Express your knowledge base using the DLGP format. e.g. kowalski.dlgp
~~~
% kowalski.dlgp
%----------------- Rules --------------------
bird(X) :- penguin(X).
nofly(X) :- penguin(X).
[DEFT] fly(X) :- bird(X).

%----------- Negative Constraints -----------
! :- nofly(X), fly(X).

%----------------- Facts --------------------
penguin(kowalski).
~~~

2. in your code, create a KB object using the dlgp file, and saturate the knowledge base
~~~
KB kb = new KB("path/to/kowalski.dlgp");
kb.saturate();
~~~

3. Transform your atomic query to an atom
~~~
Atom atom = kb.getAtomsSatisfiyingAtomicQuery("?(X) :- nofly(kowalski).").iterator().next();
~~~

4. Set the preference function to use
~~~
kb.setPreferenceFunction(new GeneralizedSpecificityPreference());
~~~

5. Get its entailment status
~~~
int entailment = kb.EntailmentStatus(a);
/* the KB class contains constants explaining the entailement status: 
NOT_ENTAILED, STRICTLY_ENTAILED, DEFEASIBLY_ENTAILED */
~~~
## Next Versions TODO List

- Add detailed documentation.
- Add preference on rules.
- Add support for default negation.
- Add a graphical tool for dialectical tree visualisation.

## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D

## License

DEFT is an open-sourced software licensed under the [MIT license](http://opensource.org/licenses/MIT)
