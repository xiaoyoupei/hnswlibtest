from setuptools import setup, find_packages
setup(
    name="pyspark_hnsw",
    url="https://github.com/jelmerk/hnswlib",
    version="0.20",
    zip_safe=True,
    packages=find_packages(),
    extras_require={'test': ['pytest', 'findspark']},
)