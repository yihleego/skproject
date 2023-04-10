import {type NextPage} from "next";
import Head from "next/head";
import Main from "~/components/Main";

const Home: NextPage = () => {
  return (
    <>
      <Head>
        <title>Spiral Knight Haven</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <meta name="keywords" content="Spiral Knights, Auction House, Haven"/>
        <meta name="description" content="Spiral Knight Haven"/>
        <link rel="icon" href="/ui/icon/app.ico"/>
      </Head>
      <Main/>
    </>
  );
};

export default Home;
